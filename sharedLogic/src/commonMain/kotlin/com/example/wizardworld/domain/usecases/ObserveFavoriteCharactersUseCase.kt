package com.example.wizardworld.domain.usecases

import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.repository.CharacterRepository
import com.example.wizardworld.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveFavoriteCharactersUseCase(
    private val favoriteRepository: FavoriteRepository,
    private val characterRepository: CharacterRepository,
) {
    operator fun invoke(): Flow<List<HpCharacter>> =
        combine(
            favoriteRepository.observeFavoriteIds(),
            characterRepository.observeCachedCharacters(),
        ) { favoriteIds, cachedCharacters ->
            cachedCharacters.filter { character -> character.id in favoriteIds }
        }
}
