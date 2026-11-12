package com.example.wizardworld.domain.usecases

import com.example.wizardworld.domain.repository.FavoriteRepository

class ToggleFavoriteUseCase(
    private val favoriteRepository: FavoriteRepository,
) {
    suspend operator fun invoke(id: String) {
        favoriteRepository.toggleFavorite(id)
    }
}
