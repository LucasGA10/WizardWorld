package com.example.wizardworld.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class InventorDto(
    val id: String,
    val firstName: String? = null,
    val lastName: String? = null,
)
