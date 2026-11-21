package com.example.wizardworld.data.remote

import io.ktor.client.engine.HttpClientEngineFactory

expect fun provideHttpClientEngine(): HttpClientEngineFactory<*>
