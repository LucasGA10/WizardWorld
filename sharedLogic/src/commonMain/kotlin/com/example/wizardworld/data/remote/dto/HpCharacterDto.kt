package com.example.wizardworld.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WandDto(
    val wood: String? = null,
    val core: String? = null,
    val length: Double? = null,
)

@Serializable
data class HpCharacterDto(
    val id: String,
    val name: String,
    @SerialName("alternate_names")
    val alternateNames: List<String> = emptyList(),
    val species: String? = null,
    val gender: String? = null,
    val house: String? = null,
    val dateOfBirth: String? = null,
    val yearOfBirth: Int? = null,
    val wizard: Boolean = true,
    val ancestry: String? = null,
    val eyeColour: String? = null,
    val hairColour: String? = null,
    val wand: WandDto? = null,
    val patronus: String? = null,
    val hogwartsStudent: Boolean = false,
    val hogwartsStaff: Boolean = false,
    val actor: String? = null,
    @SerialName("alternate_actors")
    val alternateActors: List<String> = emptyList(),
    val alive: Boolean = true,
    val image: String? = null,
)
