package com.example.wizardworld.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PotterDbPaginationDto(
    val current: Int? = null,
    val next: Int? = null,
    val last: Int? = null,
    val records: Int? = null,
)

@Serializable
data class PotterDbMetaDto(
    val pagination: PotterDbPaginationDto? = null,
    val copyright: String? = null,
    @SerialName("generated_at")
    val generatedAt: String? = null,
)

@Serializable
data class PotterDbResponse<T>(
    val data: T,
    val meta: PotterDbMetaDto? = null,
)
