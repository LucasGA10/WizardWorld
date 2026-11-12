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
    private val queries = database.hpCharacterQueries

    fun observeCharacters(): Flow<List<HpCharacter>> =
        queries.selectAllHpCharacters()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { characters -> characters.mapNotNull { decodeOrNull(it.json) } }

    suspend fun saveCharacter(character: HpCharacter) {
        withContext(Dispatchers.Default) {
            queries.insertHpCharacter(
                id = character.id,
                json = wizardWorldJson.encodeToString(HpCharacter.serializer(), character),
            )
        }
    }

    suspend fun getCharacter(id: String): HpCharacter? =
        withContext(Dispatchers.Default) {
            queries.selectHpCharacterById(id)
                .executeAsOneOrNull()
                ?.json
                ?.let(::decodeOrNull)
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
