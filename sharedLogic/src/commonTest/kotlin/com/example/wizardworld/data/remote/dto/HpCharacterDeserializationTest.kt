package com.example.wizardworld.data.remote.dto

import com.example.wizardworld.data.remote.mapper.toDomain
import com.example.wizardworld.data.remote.wizardWorldJson
import kotlinx.serialization.builtins.ListSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HpCharacterDeserializationTest {
    @Test
    fun hpCharacterJson_deserializesCorrectly() {
        val characters =
            wizardWorldJson.decodeFromString(
                ListSerializer(HpCharacterDto.serializer()),
                HP_CHARACTERS_JSON,
            )

        assertEquals(2, characters.size)

        val harry = characters.first().toDomain()
        assertEquals("9e3f7ce4-b9a7-4244-b709-dae5c1f1d4a8", harry.id)
        assertEquals("Harry Potter", harry.name)
        assertEquals("Daniel Radcliffe", harry.actor)
        assertEquals("https://ik.imagekit.io/hpapi/harry.jpg", harry.imageUrl)
        assertEquals("Gryffindor", harry.house)
        assertEquals("stag", harry.patronus)
        assertTrue(harry.isWizard)
        assertTrue(harry.isHogwartsStudent)
    }
}

private const val HP_CHARACTERS_JSON = """
[
  {
    "id": "9e3f7ce4-b9a7-4244-b709-dae5c1f1d4a8",
    "name": "Harry Potter",
    "alternate_names": ["The Boy Who Lived"],
    "species": "human",
    "gender": "male",
    "house": "Gryffindor",
    "dateOfBirth": "31-07-1980",
    "yearOfBirth": 1980,
    "wizard": true,
    "ancestry": "half-blood",
    "eyeColour": "green",
    "hairColour": "black",
    "wand": {
      "wood": "holly",
      "core": "phoenix tail feather",
      "length": 11
    },
    "patronus": "stag",
    "hogwartsStudent": true,
    "hogwartsStaff": false,
    "actor": "Daniel Radcliffe",
    "alternate_actors": [],
    "alive": true,
    "image": "https://ik.imagekit.io/hpapi/harry.jpg"
  },
  {
    "id": "4c7e6819-a91a-45b2-a454-f931e4a77cd3",
    "name": "Hermione Granger",
    "actor": "Emma Watson",
    "image": "https://ik.imagekit.io/hpapi/hermione.jpeg",
    "house": "Gryffindor"
  }
]
"""
