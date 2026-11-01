package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.usecases.GetCharacterDetailUseCase
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailState(
    val currentState: UiState<HpCharacter> = UiState.Loading,
)

class CharacterDetailViewModel(
    private val characterId: String,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        viewModelScope.launch {
            getCharacterDetailUseCase(characterId).collect { result ->
                _state.update { currentState ->
                    currentState.copy(
                        currentState = result.toUiState(),
                    )
                }
            }
        }
    }
}
