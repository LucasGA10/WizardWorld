package com.example.wizardworld.data.remote

import com.example.wizardworld.data.remote.dto.HpCharacterDto
import com.example.wizardworld.data.remote.dto.PotterDbResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class HpApi(
    private val client: HttpClient,
) {
    suspend fun getCharacters(
        nameQuery: String? = null,
        pageNumber: Int? = null,
        pageSize: Int? = null,
    ): PotterDbResponse<List<HpCharacterDto>> =
        client
            .get("characters") {
                if (!nameQuery.isNullOrBlank()) {
                    parameter("filter[name_cont]", nameQuery)
                }
                if (pageNumber != null) {
                    parameter("page[number]", pageNumber)
                }
                if (pageSize != null) {
                    parameter("page[size]", pageSize)
                }
            }.body()

    suspend fun getCharacter(id: String): List<HpCharacterDto> {
        val singleData = client.get("characters/$id").body<PotterDbResponse<HpCharacterDto>>().data
        return listOf(singleData)
    }
}
