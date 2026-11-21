package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.usecases.GetCharacterDetailUseCase
import com.example.wizardworld.domain.usecases.ObserveFavoriteIdsUseCase
import com.example.wizardworld.domain.usecases.ToggleFavoriteUseCase
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterDetailState(
    val currentState: UiState<HpCharacter> = UiState.Loading,
    val isFavorite: Boolean = false,
)

class CharacterDetailViewModel(
    private val characterId: String,
    private val getCharacterDetailUseCase: GetCharacterDetailUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterDetailState())
    val state = _state.asStateFlow()

    init {
        loadCharacter()
        observeFavorite()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            toggleFavoriteUseCase(characterId)
        }
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

    private fun observeFavorite() {
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { favoriteIds ->
                _state.update { currentState ->
                    currentState.copy(isFavorite = characterId in favoriteIds)
                }
            }
        }
    }
}
