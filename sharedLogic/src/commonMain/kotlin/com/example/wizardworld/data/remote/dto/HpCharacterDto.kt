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
    val slug: String? = null,
    @SerialName("alias_names")
    val aliasNames: List<String> = emptyList(),
    val animagus: String? = null,
    @SerialName("blood_status")
    val bloodStatus: String? = null,
    val boggart: String? = null,
    val born: String? = null,
    val died: String? = null,
    @SerialName("eye_color")
    val eyeColor: String? = null,
    @SerialName("family_members")
    val familyMembers: List<String> = emptyList(),
    val gender: String? = null,
    @SerialName("hair_color")
    val hairColor: String? = null,
    val house: String? = null,
    val image: String? = null,
    val jobs: List<String> = emptyList(),
    @SerialName("marital_status")
    val maritalStatus: String? = null,
    val nationality: String? = null,
    val patronus: String? = null,
    val romances: List<String> = emptyList(),
    @SerialName("skin_color")
    val skinColor: String? = null,
    val species: String? = null,
    val titles: List<String> = emptyList(),
    val wands: List<String> = emptyList(),
    val wiki: String? = null,
)
