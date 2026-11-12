package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
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
            val characters =
                listOf(
                    HpCharacter(
                        id = "1",
                        name = "Harry Potter",
                        alternateNames = emptyList(),
                        species = "human",
                        gender = "male",
                        house = "Gryffindor",
                        dateOfBirth = "31-07-1980",
                        yearOfBirth = 1980,
                        isWizard = true,
                        ancestry = "half-blood",
                        eyeColour = "green",
                        hairColour = "black",
                        wands = emptyList(),
                        patronus = "stag",
                        isHogwartsStudent = true,
                        isHogwartsStaff = false,
                        actor = "Daniel Radcliffe",
                        alternateActors = emptyList(),
                        isAlive = true,
                        imageUrl = "https://ik.imagekit.io/hpapi/harry.jpg",
                    ),
                )
            val page = HpCharacterPage(characters = characters)
            val useCase =
                GetCharactersUseCase(
                    FakeCharacterRepository(Resource.Loading, Resource.Success(page)),
                )

            val results = useCase().toList()

            assertEquals(listOf(Resource.Loading, Resource.Success(page)), results)
        }
    }
}

private class FakeCharacterRepository(
    private vararg val pageEmissions: Resource<HpCharacterPage>,
) : CharacterRepository {
    override fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>> = flowOf(Resource.Error("Not configured"))

    override fun getCharacters(
        nameQuery: String?,
        pageNumber: Int?,
        pageSize: Int?,
    ): Flow<Resource<HpCharacterPage>> = flowOf(*pageEmissions)

    override fun getCharacter(id: String): Flow<Resource<HpCharacter>> =
        flowOf(Resource.Error("Character details are not configured in this fake."))

    override fun observeCachedCharacters(): Flow<List<HpCharacter>> = flowOf(emptyList())
}
