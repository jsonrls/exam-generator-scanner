package com.pbec.preboardexamchecker.ui.viewmodels

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

internal fun <T> Flow<T>.reportLoadErrors(
    description: String,
    showMessage: (String) -> Unit,
): Flow<T> = catch { error ->
    if (error is CancellationException) throw error
    showMessage("$description: ${error.message ?: "Unknown error"}. Reload this screen to retry.")
}
