package com.example.wizardworld.domain.model

data class Wand(
    val wood: String?,
    val core: String?,
    val length: Double?,
)

data class HpCharacter(
    val id: String,
    val name: String,
    val alternateNames: List<String>,
    val species: String?,
    val gender: String?,
    val house: String?,
    val dateOfBirth: String?,
    val yearOfBirth: Int?,
    val isWizard: Boolean,
    val ancestry: String?,
    val eyeColour: String?,
    val hairColour: String?,
    val wand: Wand?,
    val patronus: String?,
    val isHogwartsStudent: Boolean,
    val isHogwartsStaff: Boolean,
    val actor: String?,
    val alternateActors: List<String>,
    val isAlive: Boolean,
    val imageUrl: String?,
)
