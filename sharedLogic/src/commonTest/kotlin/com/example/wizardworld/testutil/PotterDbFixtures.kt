package com.example.wizardworld.testutil

const val HARRY_ID = "530da97d-5a83-4ea6-bc15-790edf5b5efc"

const val POTTER_DB_CHARACTERS_PAGE_JSON = """
{
  "data": [
    {
      "id": "530da97d-5a83-4ea6-bc15-790edf5b5efc",
      "type": "character",
      "attributes": {
        "name": "Harry Potter",
        "alias_names": ["The Boy Who Lived"],
        "house": "Gryffindor",
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
  ],
  "meta": {
    "pagination": { "current": 1, "next": 2, "last": 50, "records": 4900 },
    "copyright": "Copyright © Potter DB 2025",
    "generated_at": "2025-01-01T00:00:00.000+00:00"
  }
}
"""

const val POTTER_DB_SINGLE_CHARACTER_JSON = """
{
  "data": {
    "id": "530da97d-5a83-4ea6-bc15-790edf5b5efc",
    "type": "character",
    "attributes": {
      "name": "Harry Potter",
      "house": "Gryffindor",
      "patronus": "Stag"
    }
  }
}
"""
