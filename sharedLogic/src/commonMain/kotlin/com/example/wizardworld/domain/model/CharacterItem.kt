package com.example.wizardworld.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ElixirItem(
    val id: String? = null,
    val name: String? = null,
)

@Serializable
data class CharacterItem(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val elixirs: List<ElixirItem> = emptyList(),
)
