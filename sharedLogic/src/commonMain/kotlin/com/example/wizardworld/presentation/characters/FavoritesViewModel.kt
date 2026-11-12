package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.usecases.ObserveFavoriteCharactersUseCase
import com.example.wizardworld.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val observeFavoriteCharactersUseCase: ObserveFavoriteCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<HpCharacter>>>(UiState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeFavoriteCharactersUseCase()
                .catch { exception ->
                    _state.value = UiState.Error(exception.message ?: FAVORITES_ERROR_MESSAGE)
                }.collect { characters ->
                    _state.value =
                        if (characters.isEmpty()) {
                            UiState.Empty
                        } else {
                            UiState.Success(characters, isOffline = false)
                        }
                }
        }
    }
}

private const val FAVORITES_ERROR_MESSAGE = "No se pudieron cargar los favoritos."
