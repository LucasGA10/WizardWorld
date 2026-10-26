package com.example.wizardworld.navigation

import kotlinx.serialization.Serializable

object Destinations {
    @Serializable
    data object HouseListRoute

    @Serializable
    data class HouseDetailRoute(
        val houseId: String,
    )
}
