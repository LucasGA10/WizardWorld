package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.PaginationMeta
import com.example.wizardworld.domain.usecases.GetCharactersUseCase
import com.example.wizardworld.presentation.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterListState(
    val currentState: UiState<List<HpCharacter>> = UiState.Loading,
    val paginationMeta: PaginationMeta? = null,
    val nameQuery: String? = null,
)

class CharacterListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state.asStateFlow()
    private var loadJob: Job? = null

    fun onNameQueryChange(query: String) {
        _state.update { it.copy(nameQuery = query) }
        loadCharacters(forceRefresh = true)
    }

    fun loadCharacters(forceRefresh: Boolean = false) {
        if (!forceRefresh && _state.value.currentState is UiState.Success) return

        loadJob?.cancel()
        val query = _state.value.nameQuery
        loadJob =
            viewModelScope.launch {
                val characters = getCharactersUseCase(nameQuery = query)
                characters.collect { result ->
                    _state.update { currentState ->
                        when (result) {
                            is Resource.Success -> {
                                currentState.copy(
                                    currentState =
                                        if (result.data.characters.isEmpty()) {
                                            UiState.Empty
                                        } else {
                                            UiState.Success(
                                                result.data.characters,
                                                isOffline = false,
                                            )
                                        },
                                    paginationMeta = result.data.paginationMeta,
                                )
                            }

                            is Resource.Offline -> {
                                currentState.copy(
                                    currentState =
                                        if (result.data.characters.isEmpty()) {
                                            UiState.Empty
                                        } else {
                                            UiState.Success(
                                                result.data.characters,
                                                isOffline = true,
                                            )
                                        },
                                    paginationMeta = result.data.paginationMeta,
                                )
                            }

                            is Resource.Error -> {
                                currentState.copy(currentState = UiState.Error(message = result.message))
                            }

                            Resource.Loading -> {
                                currentState.copy(currentState = UiState.Loading)
                            }
                        }
                    }
                }
            }
    }
}
