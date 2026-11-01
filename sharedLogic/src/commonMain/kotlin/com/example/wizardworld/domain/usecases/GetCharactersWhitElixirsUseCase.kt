package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersWhitElixirsUseCase(
    private val repository: CharacterRepository,
) {
    operator fun invoke(): Flow<Resource<List<CharacterItem>>> = repository.getCharactersWithElixirs()
}
