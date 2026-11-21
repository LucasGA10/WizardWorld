package com.example.wizardworld.data.remote

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

actual fun provideHttpClientEngine(): HttpClientEngineFactory<*> = Darwin
