package com.example.wizardworld.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HpCharacter(
    val id: String,
    val name: String,
    val alternateNames: List<String> = emptyList(),
    val species: String? = null,
    val gender: String? = null,
    val house: String? = null,
    val dateOfBirth: String? = null,
    val yearOfBirth: Int? = null,
    val isWizard: Boolean = true,
    val ancestry: String? = null,
    val eyeColour: String? = null,
    val hairColour: String? = null,
    val wands: List<String> = emptyList(),
    val patronus: String? = null,
    val isHogwartsStudent: Boolean = false,
    val isHogwartsStaff: Boolean = false,
    val actor: String? = null,
    val alternateActors: List<String> = emptyList(),
    val isAlive: Boolean = true,
    val imageUrl: String? = null,
)
