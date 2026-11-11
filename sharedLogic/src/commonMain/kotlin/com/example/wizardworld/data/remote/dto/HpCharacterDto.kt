package com.example.wizardworld.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HpCharacterDto(
    val id: String,
    val type: String? = null,
    val attributes: HpCharacterAttributesDto,
)

@Serializable
data class HpCharacterAttributesDto(
    val name: String,
    @SerialName("alias_names")
    val aliasNames: List<String> = emptyList(),
    val species: String? = null,
    val gender: String? = null,
    val house: String? = null,
    val born: String? = null,
    @SerialName("blood_status")
    val bloodStatus: String? = null,
    @SerialName("eye_color")
    val eyeColor: String? = null,
    @SerialName("hair_color")
    val hairColor: String? = null,
    val wands: List<String> = emptyList(),
    val patronus: String? = null,
    val image: String? = null,
    val wiki: String? = null,
)
