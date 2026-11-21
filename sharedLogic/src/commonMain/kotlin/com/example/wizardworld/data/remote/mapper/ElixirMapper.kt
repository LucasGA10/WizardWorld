package com.example.wizardworld.data.remote.mapper

import com.example.wizardworld.data.remote.dto.ElixirDto
import com.example.wizardworld.data.remote.dto.IngredientDto
import com.example.wizardworld.data.remote.dto.InventorDto
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.domain.model.IngredientItem
import com.example.wizardworld.domain.model.InventorItem

fun ElixirDto.toDomain(): Elixir =
    Elixir(
        id = id,
        name = name,
        effect = effect,
        sideEffects = sideEffects,
        characteristics = characteristics,
        time = time,
        difficulty = difficulty,
        ingredients = ingredients.map { it.toDomain() },
        inventors = inventors.map { it.toDomain() },
        manufacturer = manufacturer,
    )

fun IngredientDto.toDomain(): IngredientItem =
    IngredientItem(
        id = id,
        name = name,
    )

fun InventorDto.toDomain(): InventorItem =
    InventorItem(
        id = id,
        firstName = firstName,
        lastName = lastName,
    )
