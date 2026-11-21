package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
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

            val result =
                try {
                    val elixirs =
                        api.getElixirs(
                            name = name,
                            difficulty = difficulty,
                            ingredient = ingredient,
                            inventorFullName = inventorFullName,
                            manufacturer = manufacturer,
                        ).map { it.toDomain() }

                    Resource.Success(elixirs)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    Resource.Error(
                        if (exception is IOException) {
                            CONNECTION_ERROR_MESSAGE
                        } else {
                            exception.message ?: DEFAULT_ERROR_MESSAGE
                        },
                    )
                }

            emit(result)
        }

    override fun getElixir(id: String): Flow<Resource<Elixir>> =
        flow {
            emit(Resource.Loading)

            val result =
                try {
                    val elixir = api.getElixir(id).toDomain()
                    Resource.Success(elixir)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    Resource.Error(
                        if (exception is IOException) {
                            CONNECTION_ERROR_MESSAGE
                        } else {
                            exception.message ?: DEFAULT_ERROR_MESSAGE
                        },
                    )
                }

            emit(result)
        }
}

private const val DEFAULT_ERROR_MESSAGE =
    "No se pudieron cargar los elixires."

private const val CONNECTION_ERROR_MESSAGE =
    "No se pudo conectar a internet. Revisá tu conexión."
