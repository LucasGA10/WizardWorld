package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.PaginationMeta
import com.example.wizardworld.domain.usecases.GetCharactersUseCase
import com.example.wizardworld.presentation.UiState
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

    fun loadCharacters(forceRefresh: Boolean = false) {
        if (!forceRefresh && _state.value.currentState is UiState.Success) return

        viewModelScope.launch {
            getCharactersUseCase(
                nameQuery = _state.value.nameQuery,
                // Query por si se agrega busqueda
            ).collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Resource.Success -> {
                            currentState.copy(
                                currentState =
                                    UiState.Success(
                                        result.data.characters,
                                        isOffline = false,
                                    ),
                                paginationMeta = result.data.paginationMeta,
                            )
                        }

                        is Resource.Offline -> {
                            currentState.copy(
                                currentState =
                                    UiState.Success(
                                        result.data.characters,
                                        isOffline = true,
                                    ),
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
