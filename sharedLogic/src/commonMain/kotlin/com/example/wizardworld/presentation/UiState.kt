package com.example.wizardworld.presentation

import com.example.wizardworld.core.Resource

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>

    data class Success<T>(
        val data: T,
        val isOffline: Boolean,
    ) : UiState<T>

    data class Error(
        val message: String,
    ) : UiState<Nothing>

    data object Empty : UiState<Nothing>
}

fun <T> Resource<T>.toUiState(isEmpty: (T) -> Boolean = { false }): UiState<T> =
    when (this) {
        Resource.Loading -> {
            UiState.Loading
        }

        is Resource.Success -> {
            if (isEmpty(data)) {
                UiState.Empty
            } else {
                UiState.Success(data = data, isOffline = false)
            }
        }

        is Resource.Offline -> {
            if (isEmpty(data)) {
                UiState.Empty
            } else {
                UiState.Success(data = data, isOffline = true)
            }
        }

        is Resource.Error -> {
            UiState.Error(message = message)
        }
    }
