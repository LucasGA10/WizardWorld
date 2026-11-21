
package com.example.wizardworld.presentation.elixirs

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.domain.repository.ElixirRepository
import com.example.wizardworld.domain.usecases.GetElixirsUseCase
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
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ElixirListViewModelTest {
    @Test
    fun loadElixirs_whenSuccessful_updatesState() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val elixirs =
                    listOf(Elixir(id = "e1", name = "Polyjuice Potion"))
                val repository = FakeElixirRepository(Resource.Success(elixirs))
                val viewModel = ElixirListViewModel(GetElixirsUseCase(repository))

                viewModel.loadElixirs()
                advanceUntilIdle()

                val state =
                    assertIs<UiState.Success<List<Elixir>>>(
                        viewModel.state.value,
                    )

                assertEquals(elixirs, state.data)
                assertEquals(false, state.isOffline)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun loadElixirs_whenEmpty_updatesStateToEmpty() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val repository =
                    FakeElixirRepository(Resource.Success(emptyList()))
                val viewModel = ElixirListViewModel(GetElixirsUseCase(repository))

                viewModel.loadElixirs()
                advanceUntilIdle()

                assertEquals(UiState.Empty, viewModel.state.value)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun loadElixirs_whenOffline_preservesOfflineIndicator() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val elixirs =
                    listOf(Elixir(id = "e1", name = "Polyjuice Potion"))
                val repository =
                    FakeElixirRepository(
                        Resource.Offline(elixirs, "Sin conexión"),
                    )
                val viewModel = ElixirListViewModel(GetElixirsUseCase(repository))

                viewModel.loadElixirs()
                advanceUntilIdle()

                val state =
                    assertIs<UiState.Success<List<Elixir>>>(
                        viewModel.state.value,
                    )

                assertEquals(elixirs, state.data)
                assertTrue(state.isOffline)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun loadElixirs_whenError_updatesStateToError() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val repository =
                    FakeElixirRepository(Resource.Error("Error de conexión"))
                val viewModel = ElixirListViewModel(GetElixirsUseCase(repository))

                viewModel.loadElixirs()
                advanceUntilIdle()

                val state =
                    assertIs<UiState.Error>(viewModel.state.value)

                assertEquals("Error de conexión", state.message)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun loadElixirs_withoutForceRefresh_doesNotReloadSuccess() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val repository =
                    FakeElixirRepository(
                        Resource.Success(
                            listOf(Elixir(id = "e1", name = "Polyjuice Potion")),
                        ),
                    )
                val viewModel = ElixirListViewModel(GetElixirsUseCase(repository))

                viewModel.loadElixirs()
                advanceUntilIdle()

                viewModel.loadElixirs()
                advanceUntilIdle()

                assertEquals(1, repository.requestCount)
            } finally {
                Dispatchers.resetMain()
            }
        }

    @Test
    fun loadElixirs_withForceRefresh_reloadsSuccess() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher(testScheduler))
            try {
                val repository =
                    FakeElixirRepository(
                        Resource.Success(
                            listOf(Elixir(id = "e1", name = "Polyjuice Potion")),
                        ),
                    )
                val viewModel = ElixirListViewModel(GetElixirsUseCase(repository))

                viewModel.loadElixirs()
                advanceUntilIdle()

                viewModel.loadElixirs(forceRefresh = true)
                advanceUntilIdle()

                assertEquals(2, repository.requestCount)
            } finally {
                Dispatchers.resetMain()
            }
        }
}

private class FakeElixirRepository(
    private val result: Resource<List<Elixir>>,
) : ElixirRepository {
    var requestCount = 0

    override fun getElixirs(
        name: String?,
        difficulty: String?,
        ingredient: String?,
        inventorFullName: String?,
        manufacturer: String?,
    ): Flow<Resource<List<Elixir>>> {
        requestCount++
        return flowOf(Resource.Loading, result)
    }

    override fun getElixir(id: String): Flow<Resource<Elixir>> = flowOf(Resource.Error("Not configured"))
}
