
package com.example.wizardworld.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.HpCharacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HpCharacterLocalDataSource(
    database: WizardWorldDatabase,
) {
    private val queries = database.hpCharacterQueries

    fun observeCharacters(): Flow<List<HpCharacter>> =
        queries.selectAllHpCharacters()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { characters -> characters.map { Json.decodeFromString<HpCharacter>(it.json) } }

    suspend fun saveCharacter(character: HpCharacter) {
        withContext(Dispatchers.Default) {
            queries.insertHpCharacter(
                id = character.id,
                json = Json.encodeToString(character),
            )
        }
    }

    suspend fun getCharacter(id: String): HpCharacter? =
        withContext(Dispatchers.Default) {
            queries.selectHpCharacterById(id)
                .executeAsOneOrNull()
                ?.json
                ?.let { Json.decodeFromString<HpCharacter>(it) }
        }
}
