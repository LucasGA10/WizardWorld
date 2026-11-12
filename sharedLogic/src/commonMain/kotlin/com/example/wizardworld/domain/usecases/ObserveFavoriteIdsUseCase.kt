package com.example.wizardworld.domain.usecases

import com.example.wizardworld.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoriteIdsUseCase(
    private val favoriteRepository: FavoriteRepository,
) {
    operator fun invoke(): Flow<Set<String>> = favoriteRepository.observeFavoriteIds()
}
