package com.example.wizardworld.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.wizardworld.data.remote.wizardWorldJson
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.HpCharacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

class HpCharacterLocalDataSource(
    database: WizardWorldDatabase,
) {
    private val database = database
    private val queries = database.hpCharacterQueries

    fun observeCharacters(): Flow<List<HpCharacter>> =
        queries.selectAllHpCharacters()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { characters -> characters.mapNotNull { decodeOrNull(it.json) } }

    suspend fun getAllCharacters(): List<HpCharacter> =
        withContext(Dispatchers.Default) {
            queries.selectAllHpCharacters()
                .executeAsList()
                .mapNotNull { decodeOrNull(it.json) }
        }

    suspend fun saveCharacter(character: HpCharacter) {
        withContext(Dispatchers.Default) {
            insertCharacter(character)
        }
    }

    // Upserts each page's characters while preserving other previously cached pages.
    suspend fun replaceCharacters(characters: List<HpCharacter>) {
        withContext(Dispatchers.Default) {
            database.transaction {
                characters.forEach(::insertCharacter)
            }
        }
    }

    suspend fun getCharacter(id: String): HpCharacter? =
        withContext(Dispatchers.Default) {
            queries.selectHpCharacterById(id)
                .executeAsOneOrNull()
                ?.json
                ?.let(::decodeOrNull)
        }

    private fun insertCharacter(character: HpCharacter) {
        queries.insertHpCharacter(
            id = character.id,
            json = wizardWorldJson.encodeToString(HpCharacter.serializer(), character),
        )
    }

    // Las filas guardadas con una versión anterior de HpCharacter (p. ej. el campo "wand" de la HP-API)
    // se ignoran en lugar de romper la app.
    private fun decodeOrNull(json: String): HpCharacter? =
        try {
            wizardWorldJson.decodeFromString(HpCharacter.serializer(), json)
        } catch (exception: SerializationException) {
            null
        } catch (exception: IllegalArgumentException) {
            null
        }
}
