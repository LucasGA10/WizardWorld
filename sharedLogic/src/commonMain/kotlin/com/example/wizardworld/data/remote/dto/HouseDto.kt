package com.example.wizardworld.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HouseDto(
    val id: String,
    val name: String? = null,
    val houseColours: String? = null,
    val founder: String? = null,
    val animal: String? = null,
    val element: String? = null,
    val ghost: String? = null,
    val commonRoom: String? = null,
    val heads: List<HouseHeadDto>? = null,
    val traits: List<TraitDto>? = null,
)

@Serializable
data class HouseHeadDto(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
)

@Serializable
data class TraitDto(
    val id: String,
    val name: String? = null,
)
