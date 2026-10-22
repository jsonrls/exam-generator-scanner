package com.pbec.preboardexamchecker.ui.scanner

/** Limits automatic retakes; a rejected image is never silently accepted. */
internal class BlurRetryPolicy(private val maxFailures: Int = 3) {
    private var failures = 0

    fun recordFailure(): Boolean {
        failures = (failures + 1).coerceAtMost(maxFailures)
        return failures >= maxFailures
    }

    fun reset() {
        failures = 0
    }
}
