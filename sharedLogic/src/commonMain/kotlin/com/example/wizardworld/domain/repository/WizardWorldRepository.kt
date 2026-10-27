package com.example.wizardworld.domain.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import kotlinx.coroutines.flow.Flow

interface WizardWorldRepository {
    fun getCharacters(): Flow<Resource<List<CharacterItem>>>
}
