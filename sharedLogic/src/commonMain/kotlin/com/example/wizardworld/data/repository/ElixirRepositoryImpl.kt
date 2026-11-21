
package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.ElixirLocalDataSource
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.mapper.toDomain
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.domain.repository.ElixirRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class ElixirRepositoryImpl(
    private val api: WizardWorldApi,
    private val localDataSource: ElixirLocalDataSource,
) : ElixirRepository {
    override fun getElixirs(
        name: String?,
        difficulty: String?,
        ingredient: String?,
        inventorFullName: String?,
        manufacturer: String?,
    ): Flow<Resource<List<Elixir>>> =
        flow {
            emit(Resource.Loading)

            val hasFilters =
                listOf(
                    name,
                    difficulty,
                    ingredient,
                    inventorFullName,
                    manufacturer,
                ).any { !it.isNullOrBlank() }

            val result =
                try {
                    val response =
                        api.getElixirs(
                            name = name,
                            difficulty = difficulty,
                            ingredient = ingredient,
                            inventorFullName = inventorFullName,
                            manufacturer = manufacturer,
                        )

                    if (hasFilters) {
                        response.forEach { localDataSource.saveElixir(it) }
                    } else {
                        localDataSource.replaceElixirs(response)
                    }

                    Resource.Success(response.map { it.toDomain() })
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    val cached = localDataSource.getAllElixirs()

                    val filtered =
                        cached.filter { elixir ->
                            matches(name, elixir.name) &&
                                matches(difficulty, elixir.difficulty) &&
                                matches(manufacturer, elixir.manufacturer) &&
                                matchesIngredient(ingredient, elixir) &&
                                matchesInventor(inventorFullName, elixir)
                        }

                    if (filtered.isNotEmpty()) {
                        Resource.Offline(
                            data = filtered,
                            message = exception.message ?: DEFAULT_ERROR_MESSAGE,
                        )
                    } else {
                        Resource.Error(
                            message = errorMessage(exception),
                        )
                    }
                }

            emit(result)
        }

    override fun getElixir(id: String): Flow<Resource<Elixir>> =
        flow {
            emit(Resource.Loading)

            val result =
                try {
                    val response = api.getElixir(id)
                    localDataSource.saveElixir(response)
                    Resource.Success(response.toDomain())
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    val cached = localDataSource.getElixir(id)

                    if (cached != null) {
                        Resource.Offline(
                            data = cached,
                            message = exception.message ?: DEFAULT_ERROR_MESSAGE,
                        )
                    } else {
                        Resource.Error(
                            message = errorMessage(exception),
                        )
                    }
                }

            emit(result)
        }
}

private fun matches(
    filter: String?,
    value: String?,
): Boolean =
    filter.isNullOrBlank() ||
        value?.contains(filter, ignoreCase = true) == true

private fun matchesIngredient(
    filter: String?,
    elixir: Elixir,
): Boolean =
    filter.isNullOrBlank() ||
        elixir.ingredients.any {
            it.name.contains(filter, ignoreCase = true)
        }

private fun matchesInventor(
    filter: String?,
    elixir: Elixir,
): Boolean =
    filter.isNullOrBlank() ||
        elixir.inventors.any {
            val fullName =
                listOfNotNull(it.firstName, it.lastName)
                    .joinToString(" ")

            fullName.contains(filter, ignoreCase = true)
        }

private fun errorMessage(exception: Exception): String =
    if (exception is IOException) {
        CONNECTION_ERROR_MESSAGE
    } else {
        exception.message ?: DEFAULT_ERROR_MESSAGE
    }

private const val DEFAULT_ERROR_MESSAGE =
    "No se pudieron cargar los elixires."

private const val CONNECTION_ERROR_MESSAGE =
    "No se pudo conectar a internet. Revisá tu conexión."
