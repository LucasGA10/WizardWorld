package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.HpCharacterLocalDataSource
import com.example.wizardworld.data.remote.HpApi
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.mapper.toDomain
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class CharacterRepositoryImpl(
    private val hpApi: HpApi,
    private val wizardWorldApi: WizardWorldApi,
    private val localDataSource: CharacterLocalDataSource,
    private val hpLocalDataSource: HpCharacterLocalDataSource,
) : CharacterRepository {
    override fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>> =
        flow {
            emit(Resource.Loading)
            // Se emite fuera del try para no confundir errores del colector con errores de red
            val result =
                try {
                    localDataSource.replaceCharacters(wizardWorldApi.getCharacters())
                    Resource.Success(localDataSource.getCharacters())
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    val cached = localDataSource.getCharacters()
                    val message = exception.message ?: DEFAULT_ERROR_MESSAGE
                    if (cached.isNotEmpty()) {
                        Resource.Offline(data = cached, message = message)
                    } else {
                        Resource.Error(message = message)
                    }
                }
            emit(result)
        }

    override fun getCharacters(nameQuery: String?): Flow<Resource<HpCharacterPage>> =
        flow {
            emit(Resource.Loading)
            val result =
                try {
                    val response = hpApi.getCharacters(nameQuery = nameQuery)
                    val data =
                        HpCharacterPage(
                            characters = response.data.map { it.toDomain() },
                            paginationMeta = response.meta.toDomain(),
                        )
                    Resource.Success(data)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    when (exception) {
                        is IOException -> Resource.Error(CONNECTION_ERROR_MESSAGE)
                        else -> Resource.Error(exception.message ?: DEFAULT_ERROR_MESSAGE)
                    }
                }
            emit(result)
        }

    override fun getCharacter(id: String): Flow<Resource<HpCharacter>> =
        flow {
            emit(Resource.Loading)

            val result =
                try {
                    val character = hpApi.getCharacter(id).first().toDomain()

                    hpLocalDataSource.saveCharacter(character)

                    Resource.Success(character)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    val cached = hpLocalDataSource.getCharacter(id)

                    if (cached != null) {
                        Resource.Offline(
                            data = cached,
                            message =
                                exception.message
                                    ?: DEFAULT_CHARACTER_ERROR_MESSAGE,
                        )
                    } else {
                        when (exception) {
                            is IOException -> Resource.Error(CONNECTION_ERROR_MESSAGE)
                            else -> Resource.Error(exception.message ?: DEFAULT_ERROR_MESSAGE)
                        }
                    }
                }

            emit(result)
        }
}

private const val DEFAULT_ERROR_MESSAGE = "No se a podido encontrar a los magos."
private const val DEFAULT_CHARACTER_ERROR_MESSAGE = "Unable to load character."
private const val CONNECTION_ERROR_MESSAGE = "No se a podido conectar a internet, revise su conexión."
