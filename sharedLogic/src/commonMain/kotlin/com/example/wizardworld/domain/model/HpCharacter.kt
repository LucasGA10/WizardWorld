package com.example.wizardworld.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HpCharacter(
    val id: String,
    val name: String,
    val slug: String? = null,
    val alternateNames: List<String> = emptyList(),
    val animagus: String? = null,
    val boggart: String? = null,
    val dateOfBirth: String? = null,
    val died: String? = null,
    val familyMembers: List<String> = emptyList(),
    val species: String? = null,
    val gender: String? = null,
    val house: String? = null,
    val yearOfBirth: Int? = null,
    val isWizard: Boolean = true,
    val ancestry: String? = null,
    val eyeColour: String? = null,
    val hairColour: String? = null,
    val skinColor: String? = null,
    val nationality: String? = null,
    val maritalStatus: String? = null,
    val jobs: List<String> = emptyList(),
    val romances: List<String> = emptyList(),
    val titles: List<String> = emptyList(),
    val wands: List<String> = emptyList(),
    val patronus: String? = null,
    val isHogwartsStudent: Boolean = false,
    val isHogwartsStaff: Boolean = false,
    val actor: String? = null,
    val alternateActors: List<String> = emptyList(),
    val isAlive: Boolean = true,
    val imageUrl: String? = null,
    val wikiUrl: String? = null,
)
