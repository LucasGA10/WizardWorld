package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
import com.example.wizardworld.domain.model.PaginationMeta
import com.example.wizardworld.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetCharactersUseCaseTest {
    @Test
    fun invoke_emitsWhatRepositoryEmits() =
        runTest {
            val page =
                HpCharacterPage(
                    characters =
                        listOf(
                            HpCharacter(
                                id = "530da97d-5a83-4ea6-bc15-790edf5b5efc",
                                name = "Harry Potter",
                                house = "Gryffindor",
                                patronus = "Stag",
                                imageUrl = "https://res.cloudinary.com/dt2gfv2pt/image/upload/v1672325338/harry_potter.jpg",
                            ),
                        ),
                    paginationMeta = PaginationMeta(currentPage = 1, nextPage = 2, lastPage = 50, totalRecords = 4900),
                )
            val useCase =
                GetCharactersUseCase(
                    FakeCharacterRepository(Resource.Loading, Resource.Success(page)),
                )

            val results = useCase().toList()

            assertEquals(listOf(Resource.Loading, Resource.Success(page)), results)
        }

    @Test
    fun invoke_forwardsSearchAndPaginationParams() =
        runTest {
            val repository = FakeCharacterRepository(Resource.Loading)
            val useCase = GetCharactersUseCase(repository)

            useCase(nameQuery = "Harry", pageNumber = 3, pageSize = 20).toList()

            assertEquals(Triple("Harry", 3, 20), repository.lastParams)
        }
}

private class FakeCharacterRepository(
    private vararg val pageEmissions: Resource<HpCharacterPage>,
) : CharacterRepository {
    var lastParams: Triple<String?, Int?, Int?>? = null

    override fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>> = flowOf(Resource.Error("Not configured"))

    override fun getCharacters(
        nameQuery: String?,
        pageNumber: Int?,
        pageSize: Int?,
    ): Flow<Resource<HpCharacterPage>> {
        lastParams = Triple(nameQuery, pageNumber, pageSize)
        return flowOf(*pageEmissions)
    }

    override fun getCharacter(id: String): Flow<Resource<HpCharacter>> =
        flowOf(Resource.Error("Character details are not configured in this fake."))
}
