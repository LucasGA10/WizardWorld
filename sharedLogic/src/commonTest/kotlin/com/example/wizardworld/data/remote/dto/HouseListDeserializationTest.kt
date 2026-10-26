package com.example.wizardworld.data.remote.dto

import com.example.wizardworld.data.remote.mapper.toListItem
import com.example.wizardworld.data.remote.wizardWorldJson
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HouseListDeserializationTest {
    @Test
    fun houseListJson_deserializesAllHousesAndNestedFields() {
        val houses =
            wizardWorldJson.decodeFromString(
                ListSerializer(HouseDto.serializer()),
                HOUSES_LIST_JSON,
            )

        assertEquals(4, houses.size)
        assertEquals(
            listOf("Gryffindor", "Ravenclaw", "Hufflepuff", "Slytherin"),
            houses.map { it.name },
        )

        val gryffindor = houses.first()
        assertEquals("0367baf3-1cb6-4baf-bede-48e17e1cd005", gryffindor.id)
        assertEquals("Scarlet and gold", gryffindor.houseColours)
        assertEquals("Godric Gryffindor", gryffindor.founder)
        assertEquals("Lion", gryffindor.animal)
        assertEquals("Fire", gryffindor.element)
        assertEquals("Nearly-Headless Nick", gryffindor.ghost)
        assertEquals("Gryffindor Tower", gryffindor.commonRoom)
        assertEquals(2, gryffindor.heads?.size)
        assertEquals("Minerva", gryffindor.heads?.last()?.firstName)
        assertEquals("McGonagall", gryffindor.heads?.last()?.lastName)
        assertTrue(gryffindor.traits.orEmpty().any { it.name == "Bravery" })

        val listItems = houses.map { it.toListItem() }
        assertEquals("Slytherin", listItems.last().name)
        assertEquals("Serpent", listItems.last().animal)
    }
}

private const val HOUSES_LIST_JSON = """
[
  {
    "id": "0367baf3-1cb6-4baf-bede-48e17e1cd005",
    "name": "Gryffindor",
    "houseColours": "Scarlet and gold",
    "founder": "Godric Gryffindor",
    "animal": "Lion",
    "element": "Fire",
    "ghost": "Nearly-Headless Nick",
    "commonRoom": "Gryffindor Tower",
    "heads": [
      {
        "id": "9915c5f8-9177-4f63-bba8-d04387a404f9",
        "firstName": "Godric",
        "lastName": "Gryffindor"
      },
      {
        "id": "530da97d-5a83-4ea6-bc15-790edf5b5efc",
        "firstName": "Minerva",
        "lastName": "McGonagall"
      }
    ],
    "traits": [
      { "id": "1773bce8-7a22-4d57-b8e1-7e1cbe26fa2b", "name": "Courage" },
      { "id": "21f22e43-efd9-4a43-87f5-eab5fb1666ea", "name": "Chivalary" },
      { "id": "60d8f5d0-de4b-41f7-b152-40543555bf3a", "name": "Nerve" },
      { "id": "68f73efc-fae9-4a54-b3e2-04bbe72f5d63", "name": "Daring" },
      { "id": "7e6d321d-fe7d-4c05-8ad7-ddabda83d8cd", "name": "Determination" },
      { "id": "adf83e9f-859b-41c4-947d-b359a05f8f3c", "name": "Bravery" }
    ]
  },
  {
    "id": "805fd37a-65ae-4fe5-b336-d767b8b7c73a",
    "name": "Ravenclaw",
    "houseColours": "Blue and bronze",
    "founder": "Rowena Ravenclaw",
    "animal": "Eagle",
    "element": "Air",
    "ghost": "Grey Lady",
    "commonRoom": "Ravenclaw Tower",
    "heads": [
      {
        "id": "102ac5fc-db71-4055-8250-bc238cffb3d9",
        "firstName": "Filius",
        "lastName": "Flitwick"
      }
    ],
    "traits": [
      { "id": "08a54d21-6137-4eda-9c32-004706650b44", "name": "Learning" }
    ]
  },
  {
    "id": "85af6295-fd01-4170-a10b-963dd51dce14",
    "name": "Hufflepuff",
    "houseColours": "Yellow and black",
    "founder": "Helga Hufflepuff",
    "animal": "Badger",
    "element": "Earth",
    "ghost": "Fat Friar",
    "commonRoom": "Hufflepuff Basement",
    "heads": [],
    "traits": []
  },
  {
    "id": "a9704c47-f92e-40a4-8771-ed1899c9b9c1",
    "name": "Slytherin",
    "houseColours": "Green and silver",
    "founder": "Salazar Slytherin",
    "animal": "Serpent",
    "element": "Water",
    "ghost": "Bloody Baron",
    "commonRoom": "Slytherin Dungeon",
    "heads": [
      {
        "id": "5cac8ad3-b5fc-4c48-a951-990cdd5671bc",
        "firstName": "Salazar",
        "lastName": "Slytherin"
      }
    ],
    "traits": [
      { "id": "42b7c304-5e62-4fae-9e52-f8c6a106e406", "name": "Ambition" }
    ]
  }
]
"""
