package com.pbec.preboardexamchecker.ui.viewmodels

import com.pbec.preboardexamchecker.data.repository.publishSnapshot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FlowLoadErrorsTest {
    @Test fun examCollectionReportsFailureAndRetainsLastSuccessfulValue() = runBlocking {
        val messages = mutableListOf<String>()
        var exams = emptyList<Int>()
        flow {
            emit(listOf(41))
            error("permission-denied")
        }.reportLoadErrors("Unable to load exams", messages::add).collect { exams = it }

        assertEquals(listOf(41), exams)
        assertEquals(1, messages.size)
        assertTrue(messages.single().contains("Unable to load exams: permission-denied"))
    }

    @Test fun mappedAndCombinedStateFailuresAreHandledBeforeStateIn() = runBlocking {
        for (combined in listOf(false, true)) {
            val failures = mutableListOf<Throwable>()
            val messages = mutableListOf<String>()
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined +
                CoroutineExceptionHandler { _, error -> failures.add(error) })
            try {
                val source: Flow<Map<String, Int>> = if (combined) {
                    combine(flowOf(1), flowOf(2)) { _, _ ->
                        error("bad exam reference")
                    }
                } else {
                    flowOf(1).map {
                        error("bad question")
                    }
                }
                val state = source.reportLoadErrors("Unable to load bank", messages::add)
                    .stateIn(scope, SharingStarted.Eagerly, emptyMap())
                yield()
                assertEquals(emptyMap<String, Int>(), state.value)
                assertEquals(1, messages.size)
                assertTrue(failures.isEmpty())
            } finally {
                scope.cancel()
            }
        }
    }

    @Test fun malformedSnapshotReportsToUiAndRemovesListenerWithoutEscapingCallback() = runBlocking {
        var callbackReturned = false
        var listenerRemoved = false
        val messages = mutableListOf<String>()
        callbackFlow<List<Int>> {
            publishSnapshot { error("Question doc-a has no question text") }
            callbackReturned = true
            awaitClose { listenerRemoved = true }
        }.reportLoadErrors("Unable to load question banks", messages::add).collect {
            fail("Malformed questions must not become a successful empty list")
        }
        assertTrue(callbackReturned)
        assertTrue(listenerRemoved)
        assertTrue(messages.single().contains("Question doc-a has no question text"))
    }

    @Test fun listenerErrorsReachUiAndRunListenerCleanup() = runBlocking {
        var listenerRemoved = false
        val messages = mutableListOf<String>()
        callbackFlow<List<Int>> {
            close(IllegalStateException("unavailable"))
            awaitClose { listenerRemoved = true }
        }.reportLoadErrors("Unable to load question count", messages::add).collect {
            fail("Listener failure must not emit a successful value")
        }
        assertTrue(listenerRemoved)
        assertTrue(messages.single().contains("unavailable"))
    }

    @Test fun successfulSnapshotsRemainUnchanged() = runBlocking {
        val values = mutableListOf<List<Int>>()
        callbackFlow<List<Int>> {
            publishSnapshot { listOf(72, 41, 72) }
            close()
            awaitClose { }
        }.reportLoadErrors("Unable to load", { fail("Unexpected UI error: $it") }).collect(values::add)
        assertEquals(listOf(listOf(72, 41, 72)), values)
    }

    @Test fun cancellationPropagatesWithoutAUiError() = runBlocking {
        for (snapshot in listOf(false, true)) {
            val cancelled = CancellationException("screen closed")
            var reported = false
            val source = if (snapshot) callbackFlow<Int> {
                publishSnapshot { throw cancelled }
                awaitClose { }
            } else flow<Int> { throw cancelled }
            try {
                source.reportLoadErrors("Unable to load") { reported = true }.collect { }
                fail("Cancellation must propagate")
            } catch (error: CancellationException) {
                assertEquals(cancelled.message, error.message)
                // Coroutine stacktrace recovery may copy the exception across callbackFlow.
                assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === cancelled })
            }
            assertFalse(reported)
        }
    }
}
