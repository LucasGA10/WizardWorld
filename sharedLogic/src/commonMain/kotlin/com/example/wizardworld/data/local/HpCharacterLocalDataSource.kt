
package com.example.wizardworld.data.local

import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.HpCharacter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HpCharacterLocalDataSource(
    database: WizardWorldDatabase,
) {
    private val queries = database.hpCharacterQueries

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
