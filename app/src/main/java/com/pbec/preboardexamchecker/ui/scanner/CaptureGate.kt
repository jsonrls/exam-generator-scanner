package com.pbec.preboardexamchecker.ui.scanner

import java.util.concurrent.atomic.AtomicBoolean

/** Shared by the analyzer and UI so one paper cannot start two simultaneous captures. */
internal class CaptureGate {
    private val acquired = AtomicBoolean(false)
    val isBusy: Boolean get() = acquired.get()
    fun tryAcquire(): Boolean = acquired.compareAndSet(false, true)
    fun reset() { acquired.set(false) }
}
