package com.example.wizardworld.data.remote.dto

import com.example.wizardworld.data.remote.wizardWorldJson
import com.example.wizardworld.domain.model.CharacterItem
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CharacterListDeserializationTest {
    @Test
    fun wizardsJson_deserializesNullsAndEmptyElixirs() {
        val characters =
            wizardWorldJson.decodeFromString(
                ListSerializer(CharacterItem.serializer()),
                WIZARDS_LIST_JSON,
            )

        assertEquals(3, characters.size)

        val withoutFirstName = characters[0]
        assertNull(withoutFirstName.firstName)
        assertEquals("Abbott", withoutFirstName.lastName)
        assertTrue(withoutFirstName.elixirs.isEmpty())

        val withElixir = characters[1]
        assertEquals("Hermione", withElixir.firstName)
        assertEquals("Granger", withElixir.lastName)
        assertEquals(1, withElixir.elixirs.size)
        assertEquals("fd6ad48a-4ddb-4dc5-9c6c-1c0e3ef8f5a5", withElixir.elixirs.first().id)
        assertEquals("Polyjuice Potion", withElixir.elixirs.first().name)

        val withoutNames = characters[2]
        assertNull(withoutNames.firstName)
        assertNull(withoutNames.lastName)
        assertTrue(withoutNames.elixirs.isEmpty())
    }
}

// Formato de GET https://wizard-world-api.herokuapp.com/Wizards
private const val WIZARDS_LIST_JSON = """
[
  {
    "elixirs": [],
    "id": "22e8f2a8-ef6d-4a3b-a8cb-7e0e7d2d5d2a",
    "firstName": null,
    "lastName": "Abbott"
  },
  {
    "elixirs": [
      { "id": "fd6ad48a-4ddb-4dc5-9c6c-1c0e3ef8f5a5", "name": "Polyjuice Potion" }
    ],
    "id": "a1b2c3d4-0000-0000-0000-000000000002",
    "firstName": "Hermione",
    "lastName": "Granger"
  },
  {
    "elixirs": [],
    "id": "b7f1c3a0-0000-0000-0000-000000000003",
    "firstName": null,
    "lastName": null
  }
]
"""
