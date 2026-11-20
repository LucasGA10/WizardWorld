
package com.example.wizardworld.data.remote

import com.example.wizardworld.data.remote.dto.ElixirDto
import com.example.wizardworld.domain.model.CharacterItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WizardWorldApi(
    private val client: HttpClient,
) {
    // La base URL y expectSuccess vienen configurados
    // en provideHttpClient().

    suspend fun getCharacters(): List<CharacterItem> = client.get("Wizards").body()

    suspend fun getCharacter(id: String): CharacterItem = client.get("Wizards/$id").body()

    suspend fun getElixirs(
        name: String? = null,
        difficulty: String? = null,
        ingredient: String? = null,
        inventorFullName: String? = null,
        manufacturer: String? = null,
    ): List<ElixirDto> =
        client.get("Elixirs") {
            name?.let { parameter("name", it) }
            difficulty?.let { parameter("difficulty", it) }
            ingredient?.let { parameter("ingredient", it) }
            inventorFullName?.let {
                parameter("inventorFullName", it)
            }
            manufacturer?.let {
                parameter("manufacturer", it)
            }
        }.body()

    suspend fun getElixir(id: String): ElixirDto = client.get("Elixirs/$id").body()
}
