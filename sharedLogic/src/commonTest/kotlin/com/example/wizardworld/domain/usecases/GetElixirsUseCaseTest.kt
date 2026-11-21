
package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.domain.repository.ElixirRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetElixirsUseCaseTest {
    @Test
    fun invoke_preservesRepositoryStates() =
        runTest {
            val elixirs =
                listOf(
                    Elixir(id = "e1", name = "Polyjuice Potion"),
                )

            val expected =
                listOf(
                    Resource.Loading,
                    Resource.Success(elixirs),
                )

            val repository = FakeElixirRepository(expected)
            val useCase = GetElixirsUseCase(repository)

            assertEquals(expected, useCase().toList())
        }

    @Test
    fun invoke_preservesOfflineState() =
        runTest {
            val cached =
                listOf(
                    Elixir(id = "e1", name = "Polyjuice Potion"),
                )

            val expected =
                listOf(
                    Resource.Loading,
                    Resource.Offline(cached, "Sin conexión"),
                )

            val useCase =
                GetElixirsUseCase(FakeElixirRepository(expected))

            assertEquals(expected, useCase().toList())
        }

    @Test
    fun invoke_preservesErrorState() =
        runTest {
            val expected =
                listOf<Resource<List<Elixir>>>(
                    Resource.Loading,
                    Resource.Error("No hay datos disponibles"),
                )

            val useCase =
                GetElixirsUseCase(FakeElixirRepository(expected))

            assertEquals(expected, useCase().toList())
        }

    @Test
    fun invoke_forwardsAllFilters() =
        runTest {
            val repository = FakeElixirRepository(emptyList())
            val useCase = GetElixirsUseCase(repository)

            useCase(
                name = "Polyjuice",
                difficulty = "Advanced",
                ingredient = "Fluxweed",
                inventorFullName = "Hermione Granger",
                manufacturer = "Hogwarts",
            ).toList()

            assertEquals(
                ElixirFilters(
                    name = "Polyjuice",
                    difficulty = "Advanced",
                    ingredient = "Fluxweed",
                    inventorFullName = "Hermione Granger",
                    manufacturer = "Hogwarts",
                ),
                repository.lastFilters,
            )
        }
}

private data class ElixirFilters(
    val name: String?,
    val difficulty: String?,
    val ingredient: String?,
    val inventorFullName: String?,
    val manufacturer: String?,
)

private class FakeElixirRepository(
    private val emissions: List<Resource<List<Elixir>>>,
) : ElixirRepository {
    var lastFilters: ElixirFilters? = null

    override fun getElixirs(
        name: String?,
        difficulty: String?,
        ingredient: String?,
        inventorFullName: String?,
        manufacturer: String?,
    ): Flow<Resource<List<Elixir>>> {
        lastFilters =
            ElixirFilters(
                name,
                difficulty,
                ingredient,
                inventorFullName,
                manufacturer,
            )

        return flowOf(*emissions.toTypedArray())
    }

    override fun getElixir(id: String): Flow<Resource<Elixir>> = flowOf(Resource.Error("Not configured"))
}
