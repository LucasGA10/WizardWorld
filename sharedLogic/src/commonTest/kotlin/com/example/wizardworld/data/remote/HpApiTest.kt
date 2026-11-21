package com.example.wizardworld.data.remote

import com.example.wizardworld.testutil.HARRY_ID
import com.example.wizardworld.testutil.POTTER_DB_CHARACTERS_PAGE_JSON
import com.example.wizardworld.testutil.POTTER_DB_SINGLE_CHARACTER_JSON
import com.example.wizardworld.testutil.createMockHttpClient
import com.example.wizardworld.testutil.respondJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.request.HttpRequestData
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HpApiTest {
    @Test
    fun getCharacters_withoutParams_requestsCharactersPathWithoutQuery() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val api = createApi(requests, POTTER_DB_CHARACTERS_PAGE_JSON)

            val response = api.getCharacters()

            val sent = requests.single()
            assertEquals("/v1/characters", sent.url.encodedPath)
            assertTrue(sent.url.parameters.isEmpty())
            assertEquals(
                listOf("Harry Potter", "Hermione Granger"),
                response.data.map { it.attributes.name },
            )
            assertEquals(2, assertNotNull(response.meta?.pagination).next)
        }

    @Test
    fun getCharacters_withParams_sendsPotterDbFilterAndPageParams() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val api = createApi(requests, POTTER_DB_CHARACTERS_PAGE_JSON)

            api.getCharacters(nameQuery = "Harry", pageNumber = 2, pageSize = 10)

            val params = requests.single().url.parameters
            assertEquals("Harry", params["filter[name_cont]"])
            assertEquals("2", params["page[number]"])
            assertEquals("10", params["page[size]"])
        }

    @Test
    fun getCharacters_withBlankQuery_doesNotSendNameFilter() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val api = createApi(requests, POTTER_DB_CHARACTERS_PAGE_JSON)

            api.getCharacters(nameQuery = "  ")

            assertFalse(requests.single().url.parameters.contains("filter[name_cont]"))
        }

    @Test
    fun getCharacter_requestsCharacterByIdAndWrapsSingleResult() =
        runTest {
            val requests = mutableListOf<HttpRequestData>()
            val api = createApi(requests, POTTER_DB_SINGLE_CHARACTER_JSON)

            val result = api.getCharacter(HARRY_ID)

            assertEquals("/v1/characters/$HARRY_ID", requests.single().url.encodedPath)
            assertEquals(1, result.size)
            assertEquals("Harry Potter", result.first().attributes.name)
        }
}

private fun createApi(
    requests: MutableList<HttpRequestData>,
    responseJson: String,
): HpApi =
    HpApi(
        createMockHttpClient(
            engine =
                MockEngine { request ->
                    requests += request
                    respondJson(responseJson)
                },
            baseUrl = HP_API_BASE_URL,
        ),
    )
