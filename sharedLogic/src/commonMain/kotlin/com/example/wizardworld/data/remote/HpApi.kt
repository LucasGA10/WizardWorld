package com.example.wizardworld.data.remote

import com.example.wizardworld.data.remote.dto.HpCharacterDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class HpApi(
    private val client: HttpClient,
) {
    suspend fun getCharacters(): List<HpCharacterDto> = client.get("characters").body()

    suspend fun getCharacter(id: String): List<HpCharacterDto> = client.get("character/$id").body()
}
