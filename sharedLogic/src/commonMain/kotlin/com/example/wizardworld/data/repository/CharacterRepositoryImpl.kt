package com.example.wizardworld.data.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class CharacterRepositoryImpl(
    private val api: WizardWorldApi,
    private val localDataSource: CharacterLocalDataSource,
) : CharacterRepository {
    override fun getCharacters(): Flow<Resource<List<CharacterItem>>> =
        flow {
            emit(Resource.Loading)
            // Se emite fuera del try para no confundir errores del colector con errores de red
            val result =
                try {
                    localDataSource.replaceCharacters(api.getCharacters())
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

    override fun getCharacter(id: String): Flow<Resource<CharacterItem>> =
        flow {
                emit(Resource.Loading)
                val result =
                    try {
                        val character = api.getCharacter(id)
                        localDataSource.replaceCharacter(character)
                        Resource.Success(character)
                    } catch (exception: CancellationException) {
                        throw exception
                    } catch (exception: Exception) {
                        val cached = localDataSource.getCharacter(id)
                        val message = exception.message ?: DEFAULT_CHARACTER_ERROR_MESSAGE
                        if (cached != null) {
                            Resource.Offline(data = cached, message = message)
                        } else {
                            Resource.Error(message = message)
                        }
                    }
                emit(result)
        }
}

private const val DEFAULT_ERROR_MESSAGE = "Unable to load characters."
private const val DEFAULT_CHARACTER_ERROR_MESSAGE = "Unable to load character."
