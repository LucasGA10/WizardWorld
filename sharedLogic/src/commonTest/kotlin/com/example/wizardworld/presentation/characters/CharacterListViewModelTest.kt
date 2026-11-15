package com.example.wizardworld.presentation.characters

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
import com.example.wizardworld.domain.model.PaginationMeta
import com.example.wizardworld.domain.repository.CharacterRepository
import com.example.wizardworld.domain.usecases.GetCharactersUseCase
import com.example.wizardworld.presentation.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {
    @Test
    fun loadNextPage_usesPaginationMetaAndAppendsCharacters() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val firstPageCharacters = (1..10).map(::character)
                val secondPageCharacters = listOf(character(11))
                val repository =
                    FakeCharacterRepository(
                        pages =
                            mapOf(
                                1 to
                                    HpCharacterPage(
                                        characters = firstPageCharacters,
                                        paginationMeta =
                                            PaginationMeta(
                                                currentPage = 1,
                                                nextPage = 3,
                                                lastPage = 3,
                                                hasNextPage = true,
                                            ),
                                    ),
                                3 to
                                    HpCharacterPage(
                                        characters = secondPageCharacters,
                                        paginationMeta =
                                            PaginationMeta(
                                                currentPage = 3,
                                                lastPage = 3,
                                                hasNextPage = false,
                                            ),
                                    ),
                            ),
                    )
                val viewModel = CharacterListViewModel(GetCharactersUseCase(repository))

                viewModel.loadCharacters()
                advanceUntilIdle()

                viewModel.loadNextPage()
                advanceUntilIdle()

                assertEquals(
                    listOf<Triple<String?, Int?, Int?>>(
                        Triple(null, 1, 10),
                        Triple(null, 3, 10),
                    ),
                    repository.requests,
                )
                assertEquals(
                    firstPageCharacters + secondPageCharacters,
                    (viewModel.state.value.currentState as UiState.Success<List<HpCharacter>>).data,
                )
                assertEquals(false, viewModel.state.value.paginationMeta?.hasNextPage)
                viewModel.loadNextPage()
                advanceUntilIdle()
                assertEquals(2, repository.requests.size)
            } finally {
                Dispatchers.resetMain()
            }
        }
}

private class FakeCharacterRepository(
    private val pages: Map<Int, HpCharacterPage>,
) : CharacterRepository {
    val requests = mutableListOf<Triple<String?, Int?, Int?>>()

    override fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>> = flowOf(Resource.Error("Not configured"))

    override fun getCharacters(
        nameQuery: String?,
        pageNumber: Int?,
        pageSize: Int?,
    ): Flow<Resource<HpCharacterPage>> {
        requests += Triple(nameQuery, pageNumber, pageSize)
        val page = pages[pageNumber] ?: error("Unexpected page request: $pageNumber")
        return flowOf(Resource.Loading, Resource.Success(page))
    }

    override fun getCharacter(id: String): Flow<Resource<HpCharacter>> = flowOf(Resource.Error("Not configured"))

    override fun observeCachedCharacters(): Flow<List<HpCharacter>> = flowOf(emptyList())
}

private fun character(number: Int) =
    HpCharacter(
        id = number.toString(),
        name = "Character $number",
    )
