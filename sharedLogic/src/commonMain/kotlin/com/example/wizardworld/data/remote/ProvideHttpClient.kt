package com.example.wizardworld.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val wizardWorldJson =
    Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

const val WIZARD_WORLD_API_BASE_URL = "https://wizard-world-api.herokuapp.com/"

fun provideHttpClient(): HttpClient =
    HttpClient(provideHttpClientEngine()) {
        expectSuccess = true
        install(ContentNegotiation) {
            json(wizardWorldJson)
        }
        defaultRequest {
            url(WIZARD_WORLD_API_BASE_URL)
        }
    }
