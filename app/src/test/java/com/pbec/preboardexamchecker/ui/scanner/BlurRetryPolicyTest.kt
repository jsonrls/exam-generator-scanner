package com.pbec.preboardexamchecker.ui.scanner

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BlurRetryPolicyTest {
    @Test fun `third failed capture pauses automatic retries and further failures stay paused`() {
        val policy = BlurRetryPolicy()
        assertFalse(policy.recordFailure())
        assertFalse(policy.recordFailure())
        assertTrue(policy.recordFailure())
        repeat(10) { assertTrue(policy.recordFailure()) }
    }

    @Test fun `successful capture or new session resets the retry budget`() {
        val policy = BlurRetryPolicy()
        repeat(3) { policy.recordFailure() }
        policy.reset()
        assertFalse(policy.recordFailure())
        assertFalse(policy.recordFailure())
        assertTrue(policy.recordFailure())
    }
}
