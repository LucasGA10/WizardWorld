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
        val response =
            wizardWorldJson.decodeFromString(
                PotterDbResponse.serializer(ListSerializer(HpCharacterDto.serializer())),
                POTTER_DB_CHARACTERS_JSON,
            )

        val characters = response.data
        assertEquals(2, characters.size)

        val harry = characters.first().toDomain()
        assertEquals("530da97d-5a83-4ea6-bc15-790edf5b5efc", harry.id)
        assertEquals("Harry Potter", harry.name)
        assertEquals("https://res.cloudinary.com/dt2gfv2pt/image/upload/v1672325338/harry_potter.jpg", harry.imageUrl)
        assertEquals("Gryffindor", harry.house)
        assertEquals("Stag", harry.patronus)
        assertTrue(harry.isWizard)
    }
}

private const val POTTER_DB_CHARACTERS_JSON = """
{
  "data": [
    {
      "id": "530da97d-5a83-4ea6-bc15-790edf5b5efc",
      "type": "character",
      "attributes": {
        "name": "Harry Potter",
        "alias_names": ["The Boy Who Lived"],
        "species": "Human",
        "gender": "Male",
        "house": "Gryffindor",
        "born": "31 July 1980",
        "blood_status": "Half-blood",
        "eye_color": "Green",
        "hair_color": "Black",
        "wands": ["11\", Holly, phoenix feather"],
        "patronus": "Stag",
        "image": "https://res.cloudinary.com/dt2gfv2pt/image/upload/v1672325338/harry_potter.jpg"
      }
    },
    {
      "id": "4c7e6819-a91a-45b2-a454-f931e4a77cd3",
      "type": "character",
      "attributes": {
        "name": "Hermione Granger",
        "house": "Gryffindor"
      }
    }
  ]
}
"""
