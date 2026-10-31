package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCharactersUseCaseTest {
    @Test
    fun invoke_emitsWhatRepositoryEmits() {
        runBlocking {
            val characters = listOf(CharacterItem(id = "1", firstName = "Harry", lastName = "Potter"))
            val useCase =
                GetCharactersWhitElixirsUseCase(
                    FakeCharacterRepository(Resource.Loading, Resource.Success(characters)),
                )

            val results = useCase().toList()

            assertEquals(listOf(Resource.Loading, Resource.Success(characters)), results)
        }
    }
}

private class FakeCharacterRepository(
    private vararg val emissions: Resource<List<CharacterItem>>,
) : CharacterRepository {
    override fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>> = flowOf(*emissions)

    override fun getCharacters(): Flow<Resource<List<HpCharacter>>> {
        TODO("Not yet implemented")
    }

    override fun getCharacter(id: String): Flow<Resource<HpCharacter>> =
        flowOf(Resource.Error("Character details are not configured in this fake."))
}
