package com.example.wizardworld.domain.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>>

    fun getCharacters(): Flow<Resource<List<HpCharacter>>>

    fun getCharacter(id: String): Flow<Resource<HpCharacter>>
}
