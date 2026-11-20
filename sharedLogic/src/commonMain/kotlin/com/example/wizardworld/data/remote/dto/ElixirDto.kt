package com.example.wizardworld.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ElixirDto(
    val id: String,
    val name: String,
    val effect: String? = null,
    val sideEffects: String? = null,
    val characteristics: String? = null,
    val time: String? = null,
    val difficulty: String? = null,
    val ingredients: List<IngredientDto> = emptyList(),
    val inventors: List<InventorDto> = emptyList(),
    val manufacturer: String? = null,
)
