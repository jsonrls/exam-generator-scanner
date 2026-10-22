package com.pbec.preboardexamchecker.ui.scanner

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.*
import org.junit.Test

class CaptureGateTest {
    @Test fun simultaneousManualAndAutomaticRequestsCaptureOnlyOnceUntilRearmed() {
        val gate = CaptureGate()
        val start = CountDownLatch(1)
        val completed = CountDownLatch(2)
        val captures = AtomicInteger()
        val executor = Executors.newFixedThreadPool(2)
        try {
            repeat(2) {
                executor.submit {
                    start.await()
                    if (gate.tryAcquire()) captures.incrementAndGet()
                    completed.countDown()
                }
            }
            start.countDown()
            assertTrue(completed.await(5, TimeUnit.SECONDS))
            assertEquals(1, captures.get())
            assertFalse(gate.tryAcquire())
            gate.reset()
            assertTrue(gate.tryAcquire())
        } finally {
            executor.shutdownNow()
        }
    }
}
