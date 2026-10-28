package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.usecases.GetCharactersUseCase
import com.example.wizardworld.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterListState(
    val currentState: UiState<List<CharacterItem>> = UiState.Loading,
)

class CharacterListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            getCharactersUseCase().collect { result ->
                _state.update { currentState ->
                    when (result) {
                        is Resource.Success -> {
                            currentState.copy(
                                currentState =
                                    UiState.Success(
                                        result.data,
                                        isOffline = false,
                                    ),
                            )
                        }

                        is Resource.Offline -> {
                            currentState.copy(
                                currentState =
                                    UiState.Success(
                                        result.data,
                                        isOffline = true,
                                    ),
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
