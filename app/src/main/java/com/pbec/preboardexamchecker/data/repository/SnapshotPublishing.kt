package com.pbec.preboardexamchecker.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope

/** Snapshot callbacks must deliver parsing failures through the flow, never the event thread. */
@OptIn(ExperimentalCoroutinesApi::class)
internal inline fun <T> ProducerScope<T>.publishSnapshot(parse: () -> T) {
    try {
        trySend(parse())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        close(error)
    }
}
