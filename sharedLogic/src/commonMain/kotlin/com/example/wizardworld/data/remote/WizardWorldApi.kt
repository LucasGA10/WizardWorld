package com.example.wizardworld.data.remote

import com.example.wizardworld.domain.model.CharacterItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class WizardWorldApi(
    private val client: HttpClient,
) {
    // La base URL y expectSuccess ya vienen configurados en provideHttpClient()
    suspend fun getCharacters(): List<CharacterItem> = client.get("Wizards").body()

    suspend fun getCharacter(id: String): CharacterItem = client.get("Wizards/$id").body()
}
