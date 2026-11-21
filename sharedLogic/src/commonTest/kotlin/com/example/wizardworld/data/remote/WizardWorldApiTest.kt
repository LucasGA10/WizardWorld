
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
import kotlin.test.assertNull

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
            assertEquals(
                listOf("Potter", "Granger"),
                characters.map { it.lastName },
            )
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
            assertEquals(
                listOf("Polyjuice Potion"),
                character.elixirs.map { it.name },
            )
        }

    @Test
    fun getCharacters_whenServerError500_throws() =
        runTest {
            val api =
                createApi(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            assertFailsWith<ServerResponseException> {
                api.getCharacters()
            }
        }

    @Test
    fun getCharacter_whenServerError500_throws() =
        runTest {
            val api =
                createApi(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            assertFailsWith<ServerResponseException> {
                api.getCharacter(HERMIONE_ID)
            }
        }

    @Test
    fun getElixirs_requestsElixirsPathAndParsesList() =
        runTest {
            val paths = mutableListOf<String>()
            val api =
                createApi(
                    MockEngine { request ->
                        paths += request.url.encodedPath
                        respondJson(ELIXIRS_JSON)
                    },
                )

            val elixirs = api.getElixirs()

            assertEquals(listOf("/Elixirs"), paths)
            assertEquals(2, elixirs.size)
            assertEquals("Polyjuice Potion", elixirs[0].name)
            assertEquals("Felix Felicis", elixirs[1].name)
        }

    @Test
    fun getElixir_requestsElixirByIdAndParsesDetails() =
        runTest {
            val paths = mutableListOf<String>()
            val api =
                createApi(
                    MockEngine { request ->
                        paths += request.url.encodedPath
                        respondJson(ELIXIR_JSON)
                    },
                )

            val elixir = api.getElixir(ELIXIR_ID)

            assertEquals(listOf("/Elixirs/$ELIXIR_ID"), paths)
            assertEquals("Polyjuice Potion", elixir.name)
            assertEquals("Advanced", elixir.difficulty)
            assertEquals("Changes appearance", elixir.effect)

            assertEquals(2, elixir.ingredients.size)
            assertEquals("Lacewing flies", elixir.ingredients[0].name)
            assertEquals("Fluxweed", elixir.ingredients[1].name)

            assertEquals(1, elixir.inventors.size)
            assertEquals("Hermione", elixir.inventors[0].firstName)
            assertEquals("Granger", elixir.inventors[0].lastName)
        }

    @Test
    fun getElixirs_withFilters_sendsQueryParameters() =
        runTest {
            val queryParameters = mutableMapOf<String, String?>()

            val api =
                createApi(
                    MockEngine { request ->
                        listOf(
                            "name",
                            "difficulty",
                            "ingredient",
                            "inventorFullName",
                            "manufacturer",
                        ).forEach { key ->
                            queryParameters[key] =
                                request.url.parameters[key]
                        }

                        respondJson(ELIXIRS_JSON)
                    },
                )

            api.getElixirs(
                name = "Polyjuice",
                difficulty = "Advanced",
                ingredient = "Fluxweed",
                inventorFullName = "Hermione Granger",
                manufacturer = "Hogwarts",
            )

            assertEquals("Polyjuice", queryParameters["name"])
            assertEquals("Advanced", queryParameters["difficulty"])
            assertEquals("Fluxweed", queryParameters["ingredient"])
            assertEquals(
                "Hermione Granger",
                queryParameters["inventorFullName"],
            )
            assertEquals("Hogwarts", queryParameters["manufacturer"])
        }

    @Test
    fun getElixirs_withoutFilters_doesNotSendQueryParameters() =
        runTest {
            val api =
                createApi(
                    MockEngine { request ->
                        assertEquals(
                            true,
                            request.url.parameters.isEmpty(),
                        )
                        respondJson(ELIXIRS_JSON)
                    },
                )

            api.getElixirs()
        }

    @Test
    fun getElixir_withNullableFields_parsesCorrectly() =
        runTest {
            val api =
                createApi(
                    MockEngine {
                        respondJson(ELIXIR_MINIMAL_JSON)
                    },
                )

            val elixir = api.getElixir(ELIXIR_ID)

            assertEquals("Unknown Potion", elixir.name)
            assertNull(elixir.effect)
            assertNull(elixir.sideEffects)
            assertNull(elixir.difficulty)
            assertNull(elixir.manufacturer)
            assertEquals(emptyList(), elixir.ingredients)
            assertEquals(emptyList(), elixir.inventors)
        }

    @Test
    fun getElixirs_whenServerError500_throws() =
        runTest {
            val api =
                createApi(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            assertFailsWith<ServerResponseException> {
                api.getElixirs()
            }
        }

    @Test
    fun getElixir_whenServerError500_throws() =
        runTest {
            val api =
                createApi(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            assertFailsWith<ServerResponseException> {
                api.getElixir(ELIXIR_ID)
            }
        }
}

private fun createApi(engine: MockEngine): WizardWorldApi =
    WizardWorldApi(
        createMockHttpClient(
            engine,
            WIZARD_WORLD_API_BASE_URL,
        ),
    )

private const val HERMIONE_ID =
    "a1b2c3d4-0000-0000-0000-000000000002"

private const val ELIXIR_ID =
    "e1"

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
  "elixirs": [
    { "id": "e1", "name": "Polyjuice Potion" }
  ]
}
"""

private const val ELIXIRS_JSON = """
[
  {
    "id": "e1",
    "name": "Polyjuice Potion",
    "effect": "Changes appearance",
    "difficulty": "Advanced",
    "ingredients": [],
    "inventors": []
  },
  {
    "id": "e2",
    "name": "Felix Felicis",
    "effect": "Provides luck",
    "difficulty": "Advanced",
    "ingredients": [],
    "inventors": []
  }
]
"""

private const val ELIXIR_JSON = """
{
  "id": "e1",
  "name": "Polyjuice Potion",
  "effect": "Changes appearance",
  "sideEffects": null,
  "characteristics": "Thick potion",
  "time": "One month",
  "difficulty": "Advanced",
  "ingredients": [
    {
      "id": "i1",
      "name": "Lacewing flies"
    },
    {
      "id": "i2",
      "name": "Fluxweed"
    }
  ],
  "inventors": [
    {
      "id": "c1",
      "firstName": "Hermione",
      "lastName": "Granger"
    }
  ],
  "manufacturer": "Hogwarts"
}
"""

private const val ELIXIR_MINIMAL_JSON = """
{
  "id": "e1",
  "name": "Unknown Potion"
}
"""
