package com.example.wizardworld.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.HpCharacterLocalDataSource
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.data.remote.HpApi
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.wizardWorldJson
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.usecases.ObserveFavoriteCharactersUseCase
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.toUiState
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FavoriteCharacterOfflineTest {
    @Test
    fun favoriteCharacter_canBeDisplayedOfflineAfterRepositoryRecreation() {
        runBlocking {
            val driver =
                JdbcSqliteDriver(
                    url = JdbcSqliteDriver.IN_MEMORY,
                    schema = WizardWorldDatabase.Schema,
                )
            val database = createWizardWorldDatabase(driver)
            val favoriteRepository = FavoriteRepositoryImpl(database)
            val onlineRepository = createCharacterRepository(database, MockEngine { respondCharacter() })
            val characterResults = onlineRepository.getCharacter(HARRY_ID).toList()
            val cachedCharacter = assertIs<Resource.Success<HpCharacter>>(characterResults.last())
            assertEquals("Harry Potter", cachedCharacter.data.name)

            favoriteRepository.toggleFavorite(HARRY_ID)

            val offlineRepository =
                createCharacterRepository(
                    database,
                    MockEngine { respondError(HttpStatusCode.RequestTimeout) },
                )
            val offlineResult = offlineRepository.getCharacter(HARRY_ID).toList().last()
            val offlineCharacter =
                assertIs<Resource.Offline<HpCharacter>>(offlineResult)
            val detailUiState = assertIs<UiState.Success<HpCharacter>>(offlineResult.toUiState())
            val favoriteCharacters =
                ObserveFavoriteCharactersUseCase(favoriteRepository, offlineRepository)
                    .invoke()
                    .first()

            assertEquals("Harry Potter", offlineCharacter.data.name)
            assertEquals(offlineCharacter.data, detailUiState.data)
            assertEquals(true, detailUiState.isOffline)
            assertEquals(listOf(offlineCharacter.data), favoriteCharacters)
        }
    }
}

private fun createCharacterRepository(
    database: WizardWorldDatabase,
    engine: MockEngine,
): CharacterRepositoryImpl {
    val httpClient =
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(wizardWorldJson)
            }
        }
    return CharacterRepositoryImpl(
        hpApi = HpApi(httpClient),
        wizardWorldApi = WizardWorldApi(httpClient),
        localDataSource = CharacterLocalDataSource(database),
        hpLocalDataSource = HpCharacterLocalDataSource(database),
    )
}

private fun MockRequestHandleScope.respondCharacter() =
    respond(
        content = HARRY_CHARACTER_RESPONSE,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )

private const val HARRY_ID = "harry-potter"

private const val HARRY_CHARACTER_RESPONSE = """
{
  "data": {
    "id": "harry-potter",
    "type": "character",
    "attributes": {
      "name": "Harry Potter",
      "house": "Gryffindor",
      "image": "https://example.com/harry.jpg"
    }
  }
}
"""
