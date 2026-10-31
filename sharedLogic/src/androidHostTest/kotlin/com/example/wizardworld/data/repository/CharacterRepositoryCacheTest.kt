package com.example.wizardworld.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.data.remote.HpApi
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.wizardWorldJson
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.CharacterItem
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CharacterRepositoryCacheTest {
    // Varios de estos test no representan getCharacters() actualmente, sino a getCharactersWithElixirs().
    // Los test para getCharacter() se agregarán con la issue "Búsqueda y favoritos" (#27b)
    // que se encargará de guardar localmente los personajes en la pantalla de favoritos.

    @Test
    fun getCharacters_whenApiSucceeds_emitsLoadingThenSuccessFromLocal() {
        runBlocking {
            val repository = createRepository(MockEngine { respondJson(WIZARDS_JSON) })

            val results = repository.getCharactersWithElixirs().toList()

            assertEquals(Resource.Loading, results.first())
            val success = assertIs<Resource.Success<List<CharacterItem>>>(results.last())
            assertEquals(listOf("Granger", "Potter"), success.data.map { it.lastName })
            assertEquals(
                listOf("Polyjuice Potion"),
                success.data.first().elixirs.map { it.name },
            )
        }
    }

    @Test
    fun getCharacters_whenApiFailsAfterCache_emitsOffline() {
        runBlocking {
            var isOnline = true
            val repository =
                createRepository(
                    MockEngine {
                        if (isOnline) {
                            respondJson(WIZARDS_JSON)
                        } else {
                            respondError(HttpStatusCode.RequestTimeout)
                        }
                    },
                )

            assertIs<Resource.Success<*>>(repository.getCharactersWithElixirs().toList().last())
            isOnline = false

            val results = repository.getCharactersWithElixirs().toList()

            assertEquals(Resource.Loading, results.first())
            val offline = assertIs<Resource.Offline<List<CharacterItem>>>(results.last())
            assertEquals(listOf("Granger", "Potter"), offline.data.map { it.lastName })
        }
    }

    @Test
    fun getCharacters_whenApiFailsWithoutCache_emitsError() {
        runBlocking {
            val repository =
                createRepository(MockEngine { respondError(HttpStatusCode.RequestTimeout) })

            val results = repository.getCharactersWithElixirs().toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }
    }

    @Test
    fun getCharacters_whenRateLimitedAfterCache_emitsOffline() {
        runBlocking {
            var isRateLimited = false
            val repository =
                createRepository(
                    MockEngine {
                        if (isRateLimited) {
                            respondError(HttpStatusCode.TooManyRequests)
                        } else {
                            respondJson(WIZARDS_JSON)
                        }
                    },
                )

            assertIs<Resource.Success<*>>(repository.getCharactersWithElixirs().toList().last())
            isRateLimited = true

            assertIs<Resource.Offline<*>>(repository.getCharactersWithElixirs().toList().last())
        }
    }

    @Test
    fun getCharacter_whenApiFailsWithoutCache_emitsError() {
        runBlocking {
            val repository =
                createRepository(MockEngine { respondError(HttpStatusCode.RequestTimeout) })

            val results = repository.getCharacter(HERMIONE_ID).toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }
    }
}

private fun createRepository(engine: MockEngine): CharacterRepositoryImpl {
    val driver =
        JdbcSqliteDriver(
            url = JdbcSqliteDriver.IN_MEMORY,
            schema = WizardWorldDatabase.Schema,
        )
    val localDataSource = CharacterLocalDataSource(createWizardWorldDatabase(driver))
    val httpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(wizardWorldJson)
            }
        }
    return CharacterRepositoryImpl(HpApi(HttpClient()),WizardWorldApi(httpClient), localDataSource)
}

private fun MockRequestHandleScope.respondJson(json: String) =
    respond(
        content = json,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

private const val WIZARDS_JSON = """
[
  {
    "id": "a1b2c3d4-0000-0000-0000-000000000001",
    "firstName": "Harry",
    "lastName": "Potter",
    "elixirs": []
  },
  {
    "id": "a1b2c3d4-0000-0000-0000-000000000002",
    "firstName": "Hermione",
    "lastName": "Granger",
    "elixirs": [
      { "id": "e1", "name": "Polyjuice Potion" }
    ]
  }
]
"""

private const val HERMIONE_ID = "a1b2c3d4-0000-0000-0000-000000000002"

private const val CHARACTER_JSON = """
{
  "id": "a1b2c3d4-0000-0000-0000-000000000002",
  "firstName": "Hermione",
  "lastName": "Granger",
  "elixirs": [
    { "id": "e1", "name": "Polyjuice Potion" }
  ]
}
"""
