package com.pbec.preboardexamchecker.ui.handouts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pbec.preboardexamchecker.data.models.Handout
import com.pbec.preboardexamchecker.data.repository.HandoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HandoutsUiState(
    val isLoading: Boolean = true,
    val allHandouts: List<Handout> = emptyList(),
    val query: String = "",
    val selectedSubject: String? = null,
    val errorMessage: String? = null,
) {
    val visibleHandouts: List<Handout>
        get() {
            val search = query.trim()
            return allHandouts.filter { handout ->
                val matchesSubject = selectedSubject == null || handout.subject == selectedSubject
                val matchesSearch = search.isEmpty() || listOf(
                    handout.title,
                    handout.subject,
                    handout.fileName,
                ).any { value -> value.contains(search, ignoreCase = true) }
                matchesSubject && matchesSearch
            }
        }
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class HandoutsViewModel @Inject constructor(
    repository: HandoutRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedSubject = MutableStateFlow<String?>(null)
    private val retrySignal = MutableStateFlow(0)

    private val loadState = retrySignal.flatMapLatest {
        repository.observeHandouts()
            .map<List<Handout>, HandoutLoadState> { HandoutLoadState.Loaded(it) }
            .onStart { emit(HandoutLoadState.Loading) }
            .catch { error ->
                emit(
                    HandoutLoadState.Error(
                        error.message ?: "Handouts could not be loaded. Please try again.",
                    )
                )
            }
    }

    val uiState: StateFlow<HandoutsUiState> = combine(
        loadState,
        query,
        selectedSubject,
    ) { loadState, currentQuery, currentSubject ->
        when (loadState) {
            HandoutLoadState.Loading -> HandoutsUiState(
                isLoading = true,
                query = currentQuery,
                selectedSubject = currentSubject,
            )

            is HandoutLoadState.Loaded -> HandoutsUiState(
                isLoading = false,
                allHandouts = loadState.handouts,
                query = currentQuery,
                selectedSubject = currentSubject,
            )

            is HandoutLoadState.Error -> HandoutsUiState(
                isLoading = false,
                query = currentQuery,
                selectedSubject = currentSubject,
                errorMessage = loadState.message,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HandoutsUiState(),
    )

    fun setQuery(value: String) {
        query.value = value
    }

    fun setSubject(subject: String?) {
        selectedSubject.value = subject
    }

    fun retry() {
        retrySignal.value += 1
    }

    private sealed interface HandoutLoadState {
        data object Loading : HandoutLoadState
        data class Loaded(val handouts: List<Handout>) : HandoutLoadState
        data class Error(val message: String) : HandoutLoadState
    }
}
