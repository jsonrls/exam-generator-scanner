package com.pbec.preboardexamchecker.ui.scanner

object DebugFlags {
    // Opt in only during diagnostics; distributed debug APKs must not dump camera frames.
    @Volatile
    var SAVE_SCAN_DEBUG: Boolean = false
}
