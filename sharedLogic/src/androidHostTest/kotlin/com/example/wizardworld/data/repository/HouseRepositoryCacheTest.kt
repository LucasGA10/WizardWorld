package com.example.wizardworld.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.wizardworld.data.local.HouseLocalDataSource
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.data.remote.wizardWorldJson
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.model.NetworkResult
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
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class HouseRepositoryCacheTest {
    @Test
    fun getHouses_whenApiSucceeds_persistsAndReturnsSuccessFromLocal() {
        runBlocking {
            val repository = createRepository(MockEngine { respondJson(HOUSES_JSON) })

            val result = repository.getHouses()

            val success = assertIs<NetworkResult.Success<List<HouseListItem>>>(result)
            assertEquals(
                listOf("Gryffindor", "Slytherin"),
                success.data.map { it.name },
            )
        }
    }

    @Test
    fun getHouses_whenApiFailsAfterCache_returnsOffline() {
        runBlocking {
            var isOnline = true
            val repository =
                createRepository(
                    MockEngine {
                        if (isOnline) {
                            respondJson(HOUSES_JSON)
                        } else {
                            respondError(HttpStatusCode.RequestTimeout)
                        }
                    },
                )

            assertIs<NetworkResult.Success<*>>(repository.getHouses())
            isOnline = false

            val result = repository.getHouses()
            val offline = assertIs<NetworkResult.Offline<List<HouseListItem>>>(result)
            assertEquals(
                listOf("Gryffindor", "Slytherin"),
                offline.data.map { it.name },
            )
        }
    }

    @Test
    fun getHouses_whenApiFailsWithoutCache_returnsError() {
        runBlocking {
            val repository =
                createRepository(MockEngine { respondError(HttpStatusCode.RequestTimeout) })

            val result = repository.getHouses()

            assertIs<NetworkResult.Error>(result)
        }
    }

    @Test
    fun getHouse_whenApiFailsAfterCache_returnsOfflineDetail() {
        runBlocking {
            var isOnline = true
            val repository =
                createRepository(
                    MockEngine {
                        if (isOnline) {
                            respondJson(HOUSES_JSON)
                        } else {
                            respondError(HttpStatusCode.RequestTimeout)
                        }
                    },
                )

            assertIs<NetworkResult.Success<*>>(repository.getHouses())
            isOnline = false

            val result = repository.getHouse("0367baf3-1cb6-4baf-bede-48e17e1cd005")
            val offline = assertIs<NetworkResult.Offline<House>>(result)
            assertEquals("Gryffindor", offline.data.name)
            assertTrue(offline.data.traits.any { it.name == "Bravery" })
        }
    }
}

private fun createRepository(engine: MockEngine): HouseRepositoryImpl {
    val driver =
        JdbcSqliteDriver(
            url = JdbcSqliteDriver.IN_MEMORY,
            schema = WizardWorldDatabase.Schema,
        )
    val localDataSource = HouseLocalDataSource(createWizardWorldDatabase(driver))
    val httpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(wizardWorldJson)
            }
        }
    return HouseRepositoryImpl(httpClient, localDataSource)
}

private fun MockRequestHandleScope.respondJson(json: String) =
    respond(
        content = json,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

private const val HOUSES_JSON = """
[
  {
    "id": "a9704c47-f92e-40a4-8771-ed1899c9b9c1",
    "name": "Slytherin",
    "houseColours": "Green and silver",
    "founder": "Salazar Slytherin",
    "animal": "Serpent",
    "element": "Water",
    "ghost": "Bloody Baron",
    "commonRoom": "Slytherin Dungeon",
    "heads": [],
    "traits": []
  },
  {
    "id": "0367baf3-1cb6-4baf-bede-48e17e1cd005",
    "name": "Gryffindor",
    "houseColours": "Scarlet and gold",
    "founder": "Godric Gryffindor",
    "animal": "Lion",
    "element": "Fire",
    "ghost": "Nearly-Headless Nick",
    "commonRoom": "Gryffindor Tower",
    "heads": [
      {
        "id": "530da97d-5a83-4ea6-bc15-790edf5b5efc",
        "firstName": "Minerva",
        "lastName": "McGonagall"
      }
    ],
    "traits": [
      { "id": "adf83e9f-859b-41c4-947d-b359a05f8f3c", "name": "Bravery" }
    ]
  }
]
"""
