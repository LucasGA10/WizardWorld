package com.example.wizardworld.data.remote.mapper

import com.example.wizardworld.data.remote.dto.ElixirDto
import com.example.wizardworld.data.remote.dto.IngredientDto
import com.example.wizardworld.data.remote.dto.InventorDto
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ElixirMapperTest {
    @Test
    fun toDomain_mapsAllElixirFields() {
        val dto =
            ElixirDto(
                id = "e1",
                name = "Polyjuice Potion",
                effect = "Changes appearance",
                sideEffects = "Temporary transformation",
                characteristics = "Thick potion",
                time = "One month",
                difficulty = "Advanced",
                ingredients =
                    listOf(
                        IngredientDto("i1", "Fluxweed"),
                        IngredientDto("i2", "Lacewing flies"),
                    ),
                inventors =
                    listOf(
                        InventorDto(
                            id = "w1",
                            firstName = "Hermione",
                            lastName = "Granger",
                        ),
                    ),
                manufacturer = "Hogwarts",
            )

        val result = dto.toDomain()

        assertEquals("e1", result.id)
        assertEquals("Polyjuice Potion", result.name)
        assertEquals("Changes appearance", result.effect)
        assertEquals("Temporary transformation", result.sideEffects)
        assertEquals("Thick potion", result.characteristics)
        assertEquals("One month", result.time)
        assertEquals("Advanced", result.difficulty)
        assertEquals("Hogwarts", result.manufacturer)

        assertEquals(2, result.ingredients.size)
        assertEquals("i1", result.ingredients[0].id)
        assertEquals("Fluxweed", result.ingredients[0].name)
        assertEquals("Lacewing flies", result.ingredients[1].name)

        assertEquals(1, result.inventors.size)
        assertEquals("w1", result.inventors[0].id)
        assertEquals("Hermione", result.inventors[0].firstName)
        assertEquals("Granger", result.inventors[0].lastName)
    }

    @Test
    fun toDomain_preservesNullsAndEmptyLists() {
        val dto =
            ElixirDto(
                id = "e2",
                name = "Unknown Potion",
            )

        val result = dto.toDomain()

        assertEquals("e2", result.id)
        assertEquals("Unknown Potion", result.name)
        assertNull(result.effect)
        assertNull(result.sideEffects)
        assertNull(result.characteristics)
        assertNull(result.time)
        assertNull(result.difficulty)
        assertNull(result.manufacturer)
        assertTrue(result.ingredients.isEmpty())
        assertTrue(result.inventors.isEmpty())
    }
}
