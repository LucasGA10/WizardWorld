package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.HpCharacterLocalDataSource
import com.example.wizardworld.data.remote.HpApi
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.mapper.toDomain
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override fun getCharacters(): Flow<Resource<List<HpCharacter>>> =
        flow {
            emit(Resource.Loading)
            val result =
                try {
                    val characters = hpApi.getCharacters().map { it.toDomain() }
                    Resource.Success(characters)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    val message = exception.message ?: DEFAULT_ERROR_MESSAGE
                    Resource.Error(message = message)
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
                        Resource.Error(
                            message =
                                exception.message
                                    ?: DEFAULT_CHARACTER_ERROR_MESSAGE,
                        )
                    }
                }

            emit(result)
        }
}

private const val DEFAULT_ERROR_MESSAGE = "Unable to load characters."
private const val DEFAULT_CHARACTER_ERROR_MESSAGE = "Unable to load character."
