package com.example.wizardworld.domain.model

data class Elixir(
    val id: String,
    val name: String,
    val effect: String? = null,
    val sideEffects: String? = null,
    val characteristics: String? = null,
    val time: String? = null,
    val difficulty: String? = null,
    val ingredients: List<IngredientItem> = emptyList(),
    val inventors: List<InventorItem> = emptyList(),
    val manufacturer: String? = null,
)
