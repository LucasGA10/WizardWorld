package com.example.wizardworld.testutil

import com.example.wizardworld.data.remote.wizardWorldJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json

/** Mismo setup que provideHttpClient() pero con MockEngine. */
fun createMockHttpClient(
    engine: MockEngine,
    baseUrl: String,
): HttpClient =
    HttpClient(engine) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(wizardWorldJson)
        }
        defaultRequest {
            url(baseUrl)
        }
    }

fun MockRequestHandleScope.respondJson(json: String) =
    respond(
        content = json,
        status = HttpStatusCode.OK,
        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
    )
