package com.example.wizardworld.data.local

import com.example.wizardworld.db.CharacterElixirEntity
import com.example.wizardworld.db.CharacterEntity
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.ElixirItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterLocalDataSource(
    private val database: WizardWorldDatabase,
) {
    private val queries = database.characterQueries

    suspend fun getCharacters(): List<CharacterItem> =
        withContext(Dispatchers.Default) {
            queries.selectAllCharacters().executeAsList().map { character ->
                val elixirs =
                    queries.selectElixirsByCharacterId(character.id).executeAsList().map { it.toDomain() }
                character.toDomain(elixirs)
            }
        }

    suspend fun replaceCharacters(characters: List<CharacterItem>) {
        withContext(Dispatchers.Default) {
            database.transaction {
                queries.deleteAllElixirs()
                queries.deleteAllCharacters()
                characters.forEach { character ->
                    queries.insertCharacter(
                        id = character.id,
                        firstName = character.firstName,
                        lastName = character.lastName,
                    )
                    character.elixirs.forEach { elixir ->
                        queries.insertElixir(
                            characterId = character.id,
                            elixirId = elixir.id,
                            name = elixir.name,
                        )
                    }
                }
            }
        }
    }
}

private fun CharacterEntity.toDomain(elixirs: List<ElixirItem>): CharacterItem =
    CharacterItem(
        id = id,
        firstName = firstName,
        lastName = lastName,
        elixirs = elixirs,
    )

private fun CharacterElixirEntity.toDomain(): ElixirItem =
    ElixirItem(
        id = elixirId,
        name = name,
    )
