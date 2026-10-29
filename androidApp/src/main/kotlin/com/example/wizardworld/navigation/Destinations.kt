
package com.example.wizardworld.navigation

import kotlinx.serialization.Serializable

object Destinations {
    @Serializable
    data object HouseListRoute

    @Serializable
    data class HouseDetailRoute(
        val houseId: String,
    )

    @Serializable
    data object CharacterListRoute

    @Serializable
    data class CharacterDetailRoute(
        val characterId: String,
    )
}
