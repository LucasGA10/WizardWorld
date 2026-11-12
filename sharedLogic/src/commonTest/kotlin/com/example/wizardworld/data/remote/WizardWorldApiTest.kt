package com.example.wizardworld.data.remote

import com.example.wizardworld.testutil.createMockHttpClient
import com.example.wizardworld.testutil.respondJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WizardWorldApiTest {
    @Test
    fun getCharacters_requestsWizardsPathAndParsesList() =
        runTest {
            val paths = mutableListOf<String>()
            val api =
                createApi(
                    MockEngine { request ->
                        paths += request.url.encodedPath
                        respondJson(WIZARDS_JSON)
                    },
                )

            val characters = api.getCharacters()

            assertEquals(listOf("/Wizards"), paths)
            assertEquals(listOf("Potter", "Granger"), characters.map { it.lastName })
        }

    @Test
    fun getCharacter_requestsWizardByIdAndParsesCharacter() =
        runTest {
            val paths = mutableListOf<String>()
            val api =
                createApi(
                    MockEngine { request ->
                        paths += request.url.encodedPath
                        respondJson(WIZARD_JSON)
                    },
                )

            val character = api.getCharacter(HERMIONE_ID)

            assertEquals(listOf("/Wizards/$HERMIONE_ID"), paths)
            assertEquals("Hermione", character.firstName)
            assertEquals(listOf("Polyjuice Potion"), character.elixirs.map { it.name })
        }

    @Test
    fun getCharacters_whenServerError500_throws() =
        runTest {
            val api = createApi(MockEngine { respondError(HttpStatusCode.InternalServerError) })

            assertFailsWith<ServerResponseException> { api.getCharacters() }
        }

    @Test
    fun getCharacter_whenServerError500_throws() =
        runTest {
            val api = createApi(MockEngine { respondError(HttpStatusCode.InternalServerError) })

            assertFailsWith<ServerResponseException> { api.getCharacter(HERMIONE_ID) }
        }
}

private fun createApi(engine: MockEngine): WizardWorldApi = WizardWorldApi(createMockHttpClient(engine, WIZARD_WORLD_API_BASE_URL))

private const val HERMIONE_ID = "a1b2c3d4-0000-0000-0000-000000000002"

private const val WIZARDS_JSON = """
[
  { "id": "a1b2c3d4-0000-0000-0000-000000000001", "firstName": "Harry", "lastName": "Potter", "elixirs": [] },
  { "id": "a1b2c3d4-0000-0000-0000-000000000002", "firstName": "Hermione", "lastName": "Granger", "elixirs": [] }
]
"""

private const val WIZARD_JSON = """
{
  "id": "a1b2c3d4-0000-0000-0000-000000000002",
  "firstName": "Hermione",
  "lastName": "Granger",
  "elixirs": [ { "id": "e1", "name": "Polyjuice Potion" } ]
}
"""
