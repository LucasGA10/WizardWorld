
package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.remote.WIZARD_WORLD_API_BASE_URL
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.testutil.createMockHttpClient
import com.example.wizardworld.testutil.respondJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ElixirRepositoryTest {
    @Test
    fun getElixirs_emitsLoadingThenSuccess() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondJson(ELIXIRS_JSON)
                    },
                )

            val emissions = repository.getElixirs().toList()

            assertEquals(2, emissions.size)
            assertEquals(Resource.Loading, emissions[0])

            val success =
                assertIs<Resource.Success<*>>(emissions[1])

            val elixirs =
                success.data as List<*>

            assertEquals(2, elixirs.size)
        }

    @Test
    fun getElixirs_mapsApiDataToDomain() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondJson(ELIXIRS_JSON)
                    },
                )

            val emissions = repository.getElixirs().toList()
            val result = emissions.last()

            assertIs<Resource.Success<*>>(result)

            val elixirs =
                (result as Resource.Success).data

            assertEquals("Polyjuice Potion", elixirs[0].name)
            assertEquals("Advanced", elixirs[0].difficulty)
            assertEquals("Felix Felicis", elixirs[1].name)
        }

    @Test
    fun getElixirs_withEmptyResponse_emitsSuccessWithEmptyList() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondJson("[]")
                    },
                )

            val emissions = repository.getElixirs().toList()
            val result = emissions.last()

            assertIs<Resource.Success<*>>(result)
            assertTrue((result as Resource.Success).data.isEmpty())
        }

    @Test
    fun getElixirs_withFilters_forwardsParametersToApi() =
        runTest {
            val parameters = mutableMapOf<String, String?>()

            val repository =
                createRepository(
                    MockEngine { request ->
                        listOf(
                            "name",
                            "difficulty",
                            "ingredient",
                            "inventorFullName",
                            "manufacturer",
                        ).forEach { key ->
                            parameters[key] =
                                request.url.parameters[key]
                        }

                        respondJson("[]")
                    },
                )

            repository.getElixirs(
                name = "Polyjuice",
                difficulty = "Advanced",
                ingredient = "Fluxweed",
                inventorFullName = "Hermione Granger",
                manufacturer = "Hogwarts",
            ).toList()

            assertEquals("Polyjuice", parameters["name"])
            assertEquals("Advanced", parameters["difficulty"])
            assertEquals("Fluxweed", parameters["ingredient"])
            assertEquals(
                "Hermione Granger",
                parameters["inventorFullName"],
            )
            assertEquals("Hogwarts", parameters["manufacturer"])
        }

    @Test
    fun getElixir_emitsLoadingThenSuccess() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondJson(ELIXIR_JSON)
                    },
                )

            val emissions = repository.getElixir("e1").toList()

            assertEquals(2, emissions.size)
            assertEquals(Resource.Loading, emissions[0])

            val result = emissions.last()
            assertIs<Resource.Success<*>>(result)

            val elixir = (result as Resource.Success).data

            assertEquals("e1", elixir.id)
            assertEquals("Polyjuice Potion", elixir.name)
            assertEquals("Fluxweed", elixir.ingredients[0].name)
            assertEquals("Hermione", elixir.inventors[0].firstName)
        }

    @Test
    fun getElixirs_whenServerFails_emitsError() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            val emissions = repository.getElixirs().toList()

            assertEquals(Resource.Loading, emissions.first())
            assertIs<Resource.Error>(emissions.last())
        }

    @Test
    fun getElixir_whenServerFails_emitsError() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            val emissions = repository.getElixir("e1").toList()

            assertEquals(Resource.Loading, emissions.first())
            assertIs<Resource.Error>(emissions.last())
        }

    @Test
    fun getElixir_whenNotFound_emitsError() =
        runTest {
            val repository =
                createRepository(
                    MockEngine {
                        respondError(HttpStatusCode.NotFound)
                    },
                )

            val emissions = repository.getElixir("unknown").toList()

            assertEquals(Resource.Loading, emissions.first())
            assertIs<Resource.Error>(emissions.last())
        }
}

private fun createRepository(engine: MockEngine): ElixirRepositoryImpl =
    ElixirRepositoryImpl(
        WizardWorldApi(
            createMockHttpClient(
                engine,
                WIZARD_WORLD_API_BASE_URL,
            ),
        ),
    )

private const val ELIXIRS_JSON = """
[
  {
    "id": "e1",
    "name": "Polyjuice Potion",
    "difficulty": "Advanced",
    "ingredients": [],
    "inventors": []
  },
  {
    "id": "e2",
    "name": "Felix Felicis",
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
  "difficulty": "Advanced",
  "ingredients": [
    {
      "id": "i1",
      "name": "Fluxweed"
    }
  ],
  "inventors": [
    {
      "id": "w1",
      "firstName": "Hermione",
      "lastName": "Granger"
    }
  ]
}
"""
