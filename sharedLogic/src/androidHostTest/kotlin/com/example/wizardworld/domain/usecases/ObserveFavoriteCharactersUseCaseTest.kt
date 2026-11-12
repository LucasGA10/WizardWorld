package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.CharacterItem
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.HpCharacterPage
import com.example.wizardworld.domain.repository.CharacterRepository
import com.example.wizardworld.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveFavoriteCharactersUseCaseTest {
    @Test
    fun invoke_returnsOnlyFavoriteCharactersAvailableInCache() {
        runBlocking {
            val favoriteCharacter = HpCharacter(id = "favorite", name = "Harry Potter")
            val cachedCharacters =
                listOf(
                    favoriteCharacter,
                    HpCharacter(id = "not-favorite", name = "Hermione Granger"),
                )
            val useCase =
                ObserveFavoriteCharactersUseCase(
                    favoriteRepository = FakeFavoriteRepository(setOf("favorite", "not-cached")),
                    characterRepository = CachedCharacterRepositoryFake(cachedCharacters),
                )

            assertEquals(listOf(favoriteCharacter), useCase().first())
        }
    }
}

private class FakeFavoriteRepository(
    private val favoriteIds: Set<String>,
) : FavoriteRepository {
    override fun observeFavoriteIds(): Flow<Set<String>> = flowOf(favoriteIds)

    override suspend fun toggleFavorite(id: String) = Unit
}

private class CachedCharacterRepositoryFake(
    private val cachedCharacters: List<HpCharacter>,
) : CharacterRepository {
    override fun getCharactersWithElixirs(): Flow<Resource<List<CharacterItem>>> {
        return flowOf(Resource.Error("Not configured"))
    }

    override fun getCharacters(
        nameQuery: String?,
        pageNumber: Int?,
        pageSize: Int?,
    ): Flow<Resource<HpCharacterPage>> {
        return flowOf(Resource.Error("Not configured"))
    }

    override fun getCharacter(id: String): Flow<Resource<HpCharacter>> {
        return flowOf(Resource.Error("Not configured"))
    }

    override fun observeCachedCharacters(): Flow<List<HpCharacter>> {
        return flowOf(cachedCharacters)
    }
}
