
package com.example.wizardworld.data.local

import com.example.wizardworld.data.remote.dto.ElixirDto
import com.example.wizardworld.data.remote.mapper.toDomain
import com.example.wizardworld.data.remote.wizardWorldJson
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.Elixir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

class ElixirLocalDataSource(
    private val database: WizardWorldDatabase,
) {
    private val queries = database.elixirQueries

    suspend fun getAllElixirs(): List<Elixir> =
        withContext(Dispatchers.Default) {
            queries.selectAllElixirs()
                .executeAsList()
                .mapNotNull { decodeOrNull(it.json) }
        }

    suspend fun getElixir(id: String): Elixir? =
        withContext(Dispatchers.Default) {
            queries.selectElixirById(id)
                .executeAsOneOrNull()
                ?.json
                ?.let(::decodeOrNull)
        }

    suspend fun saveElixir(elixir: ElixirDto) {
        withContext(Dispatchers.Default) {
            insertElixir(elixir)
        }
    }

    suspend fun replaceElixirs(elixirs: List<ElixirDto>) {
        withContext(Dispatchers.Default) {
            database.transaction {
                queries.deleteAllElixirs()
                elixirs.forEach(::insertElixir)
            }
        }
    }

    private fun insertElixir(elixir: ElixirDto) {
        queries.insertElixir(
            id = elixir.id,
            json =
                wizardWorldJson.encodeToString(
                    ElixirDto.serializer(),
                    elixir,
                ),
        )
    }

    private fun decodeOrNull(json: String): Elixir? =
        try {
            wizardWorldJson.decodeFromString(
                ElixirDto.serializer(),
                json,
            ).toDomain()
        } catch (exception: SerializationException) {
            null
        } catch (exception: IllegalArgumentException) {
            null
        }
}
