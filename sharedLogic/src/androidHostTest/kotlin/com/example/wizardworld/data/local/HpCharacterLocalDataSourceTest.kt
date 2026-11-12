package com.example.wizardworld.data.local

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.HpCharacter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HpCharacterLocalDataSourceTest {
    @Test
    fun observeCharacters_withLegacyHpApiRow_ignoresUnknownKeysInsteadOfCrashing() {
        runBlocking {
            val database = createDatabase()
            database.hpCharacterQueries.insertHpCharacter(id = LEGACY_ID, json = LEGACY_HP_API_JSON)

            val characters = HpCharacterLocalDataSource(database).observeCharacters().first()

            assertEquals(listOf("Harry Potter"), characters.map { it.name })
        }
    }

    @Test
    fun observeCharacters_withUnreadableRow_skipsItAndKeepsValidOnes() {
        runBlocking {
            val database = createDatabase()
            val dataSource = HpCharacterLocalDataSource(database)
            dataSource.saveCharacter(HpCharacter(id = "valid", name = "Hermione Granger"))
            database.hpCharacterQueries.insertHpCharacter(id = "broken", json = """{"id":"broken"}""")

            val characters = dataSource.observeCharacters().first()

            assertEquals(listOf("Hermione Granger"), characters.map { it.name })
        }
    }

    @Test
    fun getCharacter_withUnreadableRow_returnsNull() {
        runBlocking {
            val database = createDatabase()
            database.hpCharacterQueries.insertHpCharacter(id = "broken", json = "not json")

            assertNull(HpCharacterLocalDataSource(database).getCharacter("broken"))
        }
    }

    @Test
    fun saveCharacter_thenGetCharacter_returnsSameCharacter() {
        runBlocking {
            val dataSource = HpCharacterLocalDataSource(createDatabase())
            val character = HpCharacter(id = "1", name = "Harry Potter", wands = listOf("Holly, phoenix feather"))

            dataSource.saveCharacter(character)

            assertEquals(character, dataSource.getCharacter("1"))
        }
    }
}

private fun createDatabase(): WizardWorldDatabase =
    createWizardWorldDatabase(
        JdbcSqliteDriver(
            url = JdbcSqliteDriver.IN_MEMORY,
            schema = WizardWorldDatabase.Schema,
        ),
    )

private const val LEGACY_ID = "9e3f7ce4-b9a7-4244-b709-dae5c1f1d4a8"

// Fila guardada por la versión de la app que usaba la HP-API (campo "wand" como objeto).
private const val LEGACY_HP_API_JSON = """
{
  "id": "9e3f7ce4-b9a7-4244-b709-dae5c1f1d4a8",
  "name": "Harry Potter",
  "alternateNames": ["The Boy Who Lived"],
  "species": "human",
  "gender": "male",
  "house": "Gryffindor",
  "eyeColour": "green",
  "hairColour": "black",
  "wand": { "wood": "holly", "core": "phoenix tail feather", "length": 11 },
  "patronus": "stag",
  "actor": "Daniel Radcliffe"
}
"""
