package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.HpCharacterLocalDataSource
import com.example.wizardworld.data.remote.HP_API_BASE_URL
import com.example.wizardworld.data.remote.HpApi
import com.example.wizardworld.data.remote.WIZARD_WORLD_API_BASE_URL
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
import com.example.wizardworld.testutil.HARRY_ID
import com.example.wizardworld.testutil.POTTER_DB_CHARACTERS_PAGE_JSON
import com.example.wizardworld.testutil.POTTER_DB_SINGLE_CHARACTER_JSON
import com.example.wizardworld.testutil.createMockHttpClient
import com.example.wizardworld.testutil.createTestDatabase
import com.example.wizardworld.testutil.respondJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CharacterRepositoryTest {
    @Test
    fun getCharacters_whenApiReturns200_emitsLoadingThenSuccessWithPage() =
        runTest {
            val repository = createFixture(MockEngine { respondJson(POTTER_DB_CHARACTERS_PAGE_JSON) }).repository

            val results = repository.getCharacters().toList()

            assertEquals(2, results.size)
            assertEquals(Resource.Loading, results.first())
            val page = assertIs<Resource.Success<HpCharacterPage>>(results.last()).data
            assertEquals(listOf("Harry Potter", "Hermione Granger"), page.characters.map { it.name })
            assertEquals("Stag", page.characters.first().patronus)
            val meta = assertNotNull(page.paginationMeta)
            assertEquals(1, meta.currentPage)
            assertEquals(2, meta.nextPage)
            assertEquals(50, meta.lastPage)
            assertEquals(4900, meta.totalRecords)
            assertTrue(meta.hasNextPage)
        }

    @Test
    fun getCharacters_whenTimeout408_emitsLoadingThenError() =
        runTest {
            val repository = createFixture(MockEngine { respondError(HttpStatusCode.RequestTimeout) }).repository

            val results = repository.getCharacters().toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }

    @Test
    fun getCharacters_whenServerError500_emitsLoadingThenError() =
        runTest {
            val repository = createFixture(MockEngine { respondError(HttpStatusCode.InternalServerError) }).repository

            val results = repository.getCharacters().toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }

    @Test
    fun getCharacters_whenNoConnection_emitsConnectionError() =
        runTest {
            val repository = createFixture(MockEngine { throw IOException("Network unreachable") }).repository

            val results = repository.getCharacters().toList()

            assertEquals(Resource.Loading, results.first())
            assertEquals(Resource.Error(CONNECTION_ERROR_MESSAGE), results.last())
        }

    @Test
    fun getCharacter_whenApiReturns200_emitsSuccessAndCachesCharacter() =
        runTest {
            val fixture = createFixture(MockEngine { respondJson(POTTER_DB_SINGLE_CHARACTER_JSON) })

            val results = fixture.repository.getCharacter(HARRY_ID).toList()

            assertEquals(Resource.Loading, results.first())
            val character = assertIs<Resource.Success<HpCharacter>>(results.last()).data
            assertEquals(HARRY_ID, character.id)
            assertEquals("Harry Potter", character.name)
            assertEquals("Gryffindor", character.house)
            assertEquals(character, fixture.hpLocalDataSource.getCharacter(HARRY_ID))
        }

    @Test
    fun getCharacter_whenApiFailsAfterCache_emitsOffline() =
        runTest {
            var isOnline = true
            val repository =
                createFixture(
                    MockEngine {
                        if (isOnline) {
                            respondJson(POTTER_DB_SINGLE_CHARACTER_JSON)
                        } else {
                            respondError(HttpStatusCode.InternalServerError)
                        }
                    },
                ).repository
            assertIs<Resource.Success<*>>(repository.getCharacter(HARRY_ID).toList().last())
            isOnline = false

            val results = repository.getCharacter(HARRY_ID).toList()

            assertEquals(Resource.Loading, results.first())
            val offline = assertIs<Resource.Offline<HpCharacter>>(results.last())
            assertEquals("Harry Potter", offline.data.name)
        }

    @Test
    fun getCharacter_whenApiFailsWithoutCache_emitsError() =
        runTest {
            val repository = createFixture(MockEngine { respondError(HttpStatusCode.RequestTimeout) }).repository

            val results = repository.getCharacter(HARRY_ID).toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }

    @Test
    fun getCharacter_whenNoConnectionWithoutCache_emitsConnectionError() =
        runTest {
            val repository = createFixture(MockEngine { throw IOException("Network unreachable") }).repository

            val results = repository.getCharacter(HARRY_ID).toList()

            assertEquals(Resource.Error(CONNECTION_ERROR_MESSAGE), results.last())
        }
}

private const val CONNECTION_ERROR_MESSAGE = "No se a podido conectar a internet, revise su conexión."

private class Fixture(
    val repository: CharacterRepositoryImpl,
    val hpLocalDataSource: HpCharacterLocalDataSource,
)

private fun createFixture(engine: MockEngine): Fixture {
    val database = createTestDatabase()
    val hpLocalDataSource = HpCharacterLocalDataSource(database)
    val repository =
        CharacterRepositoryImpl(
            hpApi = HpApi(createMockHttpClient(engine, HP_API_BASE_URL)),
            wizardWorldApi = WizardWorldApi(createMockHttpClient(engine, WIZARD_WORLD_API_BASE_URL)),
            localDataSource = CharacterLocalDataSource(database),
            hpLocalDataSource = hpLocalDataSource,
        )
    return Fixture(repository, hpLocalDataSource)
}
