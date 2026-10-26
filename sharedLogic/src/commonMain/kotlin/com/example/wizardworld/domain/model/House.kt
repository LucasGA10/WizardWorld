package com.example.wizardworld.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class HouseListItem(
    val id: String,
    val name: String,
    val houseColours: String,
    val founder: String,
    val animal: String,
    val element: String,
)

@Serializable
data class House(
    val id: String,
    val name: String,
    val houseColours: String,
    val founder: String,
    val animal: String,
    val element: String,
    val ghost: String,
    val commonRoom: String,
    val heads: List<HouseHead>,
    val traits: List<Trait>,
)

@Serializable
data class HouseHead(
    val id: String,
    val firstName: String,
    val lastName: String,
)

@Serializable
data class Trait(
    val id: String,
    val name: String,
)
