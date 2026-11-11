package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.HpCharacterPage
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCase(
    private val repository: CharacterRepository,
) {
    operator fun invoke(nameQuery: String? = null): Flow<Resource<HpCharacterPage>> = repository.getCharacters(nameQuery)
}
