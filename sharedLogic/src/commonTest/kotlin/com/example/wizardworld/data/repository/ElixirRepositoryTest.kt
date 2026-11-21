
package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.ElixirLocalDataSource
import com.example.wizardworld.data.remote.WIZARD_WORLD_API_BASE_URL
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.testutil.createMockHttpClient
import com.example.wizardworld.testutil.createTestDatabase
import com.example.wizardworld.testutil.respondJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ElixirRepositoryTest {
    @Test
    fun getElixirs_emitsLoadingThenSuccess() =
        runTest {
            val fixture =
                createFixture(MockEngine { respondJson(ELIXIRS_JSON) })

            val results = fixture.repository.getElixirs().toList()

            assertEquals(Resource.Loading, results.first())
            val success =
                assertIs<Resource.Success<List<Elixir>>>(results.last())

            assertEquals(2, success.data.size)
            assertEquals("Polyjuice Potion", success.data[0].name)
            assertEquals("Felix Felicis", success.data[1].name)
        }

    @Test
    fun getElixirs_savesResultsInCache() =
        runTest {
            val fixture =
                createFixture(MockEngine { respondJson(ELIXIRS_JSON) })

            fixture.repository.getElixirs().toList()

            val cached = fixture.localDataSource.getAllElixirs()

            assertEquals(2, cached.size)
            assertEquals(
                setOf("e1", "e2"),
                cached.map { it.id }.toSet(),
            )
        }

    @Test
    fun getElixirs_withEmptyResponse_emitsSuccessWithEmptyList() =
        runTest {
            val fixture =
                createFixture(MockEngine { respondJson("[]") })

            val results = fixture.repository.getElixirs().toList()

            val success =
                assertIs<Resource.Success<List<Elixir>>>(results.last())

            assertTrue(success.data.isEmpty())
        }

    @Test
    fun getElixirs_withFilters_forwardsParametersToApi() =
        runTest {
            val parameters = mutableMapOf<String, String?>()

            val fixture =
                createFixture(
                    MockEngine { request ->
                        listOf(
                            "name",
                            "difficulty",
                            "ingredient",
                            "inventorFullName",
                            "manufacturer",
                        ).forEach { key ->
                            parameters[key] = request.url.parameters[key]
                        }

                        respondJson("[]")
                    },
                )

            fixture.repository.getElixirs(
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
    fun getElixirs_filteredSearchPreservesExistingCache() =
        runTest {
            var filteredSearch = false

            val fixture =
                createFixture(
                    MockEngine {
                        if (filteredSearch) {
                            respondJson(FILTERED_ELIXIRS_JSON)
                        } else {
                            respondJson(ELIXIRS_JSON)
                        }
                    },
                )

            fixture.repository.getElixirs().toList()

            filteredSearch = true
            fixture.repository.getElixirs(name = "Polyjuice").toList()

            val cached = fixture.localDataSource.getAllElixirs()

            assertEquals(2, cached.size)
            assertEquals(
                setOf("e1", "e2"),
                cached.map { it.id }.toSet(),
            )
        }

    @Test
    fun getElixirs_whenOfflineWithCache_emitsOffline() =
        runTest {
            var online = true

            val fixture =
                createFixture(
                    MockEngine {
                        if (online) {
                            respondJson(ELIXIRS_JSON)
                        } else {
                            respondError(HttpStatusCode.InternalServerError)
                        }
                    },
                )

            fixture.repository.getElixirs().toList()
            online = false

            val results = fixture.repository.getElixirs().toList()

            assertEquals(Resource.Loading, results.first())

            val offline =
                assertIs<Resource.Offline<List<Elixir>>>(results.last())

            assertEquals(2, offline.data.size)
            assertEquals("Polyjuice Potion", offline.data[0].name)
        }

    @Test
    fun getElixirs_whenOfflineWithFilters_returnsMatchingCache() =
        runTest {
            var online = true

            val fixture =
                createFixture(
                    MockEngine {
                        if (online) {
                            respondJson(ELIXIRS_JSON)
                        } else {
                            respondError(HttpStatusCode.InternalServerError)
                        }
                    },
                )

            fixture.repository.getElixirs().toList()
            online = false

            val results =
                fixture.repository.getElixirs(
                    name = "Polyjuice",
                ).toList()

            val offline =
                assertIs<Resource.Offline<List<Elixir>>>(results.last())

            assertEquals(1, offline.data.size)
            assertEquals("e1", offline.data.first().id)
        }

    @Test
    fun getElixirs_whenOfflineWithoutCache_emitsError() =
        runTest {
            val fixture =
                createFixture(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            val results = fixture.repository.getElixirs().toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }

    @Test
    fun getElixir_emitsSuccessAndSavesInCache() =
        runTest {
            val fixture =
                createFixture(MockEngine { respondJson(ELIXIR_JSON) })

            val results = fixture.repository.getElixir("e1").toList()

            assertEquals(Resource.Loading, results.first())

            val success =
                assertIs<Resource.Success<Elixir>>(results.last())

            assertEquals("Polyjuice Potion", success.data.name)
            assertEquals("Fluxweed", success.data.ingredients[0].name)

            val cached = fixture.localDataSource.getElixir("e1")

            assertNotNull(cached)
            assertEquals(success.data, cached)
        }

    @Test
    fun getElixir_whenOfflineWithCache_emitsOffline() =
        runTest {
            var online = true

            val fixture =
                createFixture(
                    MockEngine {
                        if (online) {
                            respondJson(ELIXIR_JSON)
                        } else {
                            respondError(HttpStatusCode.InternalServerError)
                        }
                    },
                )

            fixture.repository.getElixir("e1").toList()
            online = false

            val results = fixture.repository.getElixir("e1").toList()

            assertEquals(Resource.Loading, results.first())

            val offline =
                assertIs<Resource.Offline<Elixir>>(results.last())

            assertEquals("e1", offline.data.id)
            assertEquals("Polyjuice Potion", offline.data.name)
        }

    @Test
    fun getElixir_whenOfflineWithoutCache_emitsError() =
        runTest {
            val fixture =
                createFixture(
                    MockEngine {
                        respondError(HttpStatusCode.InternalServerError)
                    },
                )

            val results = fixture.repository.getElixir("e1").toList()

            assertEquals(Resource.Loading, results.first())
            assertIs<Resource.Error>(results.last())
        }

    @Test
    fun getElixir_whenNotFound_emitsError() =
        runTest {
            val fixture =
                createFixture(
                    MockEngine {
                        respondError(HttpStatusCode.NotFound)
                    },
                )

            val results = fixture.repository.getElixir("unknown").toList()

            assertIs<Resource.Error>(results.last())
        }

    @Test
    fun getElixirs_newFullResponseReplacesOldCache() =
        runTest {
            var useNewResponse = false

            val fixture =
                createFixture(
                    MockEngine {
                        if (useNewResponse) {
                            respondJson(FILTERED_ELIXIRS_JSON)
                        } else {
                            respondJson(ELIXIRS_JSON)
                        }
                    },
                )

            fixture.repository.getElixirs().toList()
            useNewResponse = true
            fixture.repository.getElixirs().toList()

            val cached = fixture.localDataSource.getAllElixirs()

            assertEquals(1, cached.size)
            assertEquals("e1", cached.first().id)
            assertNull(fixture.localDataSource.getElixir("e2"))
        }
}

private class ElixirFixture(
    val repository: ElixirRepositoryImpl,
    val localDataSource: ElixirLocalDataSource,
)

private fun createFixture(engine: MockEngine): ElixirFixture {
    val database = createTestDatabase()
    val localDataSource = ElixirLocalDataSource(database)

    val repository =
        ElixirRepositoryImpl(
            api =
                WizardWorldApi(
                    createMockHttpClient(
                        engine,
                        WIZARD_WORLD_API_BASE_URL,
                    ),
                ),
            localDataSource = localDataSource,
        )

    return ElixirFixture(repository, localDataSource)
}

private const val ELIXIRS_JSON = """
[
  {
    "id": "e1",
    "name": "Polyjuice Potion",
    "difficulty": "Advanced",
    "ingredients": [
      { "id": "i1", "name": "Fluxweed" }
    ],
    "inventors": [
      {
        "id": "w1",
        "firstName": "Hermione",
        "lastName": "Granger"
      }
    ]
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

private const val FILTERED_ELIXIRS_JSON = """
[
  {
    "id": "e1",
    "name": "Polyjuice Potion",
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
    { "id": "i1", "name": "Fluxweed" }
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
