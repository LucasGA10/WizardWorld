package com.example.wizardworld.domain.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>>

    fun getCharacters(
        nameQuery: String? = null,
        pageNumber: Int? = null,
        pageSize: Int? = null,
    ): Flow<Resource<HpCharacterPage>>

    fun getCharacter(id: String): Flow<Resource<HpCharacter>>

    fun observeCachedCharacters(): Flow<List<HpCharacter>>
}
