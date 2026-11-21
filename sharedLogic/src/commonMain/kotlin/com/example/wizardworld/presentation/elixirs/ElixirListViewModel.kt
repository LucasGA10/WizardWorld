
package com.example.wizardworld.presentation.elixirs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.domain.usecases.GetElixirsUseCase
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.toUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ElixirListViewModel(
    private val getElixirsUseCase: GetElixirsUseCase,
) : ViewModel() {
    private val _state =
        MutableStateFlow<UiState<List<Elixir>>>(UiState.Loading)

    val state = _state.asStateFlow()

    private var loadJob: Job? = null

    fun loadElixirs(forceRefresh: Boolean = false) {
        if (!forceRefresh && _state.value is UiState.Success) return

        loadJob?.cancel()

        loadJob =
            viewModelScope.launch {
                getElixirsUseCase().collect { result ->
                    _state.value =
                        result.toUiState { elixirs ->
                            elixirs.isEmpty()
                        }
                }
            }
    }
}
