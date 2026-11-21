package com.example.wizardworld.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.PaginationMeta
import com.example.wizardworld.domain.usecases.GetCharactersUseCase
import com.example.wizardworld.presentation.UiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CharacterListState(
    val currentState: UiState<List<HpCharacter>> = UiState.Loading,
    val paginationMeta: PaginationMeta? = null,
    val nameQuery: String? = null,
    val isRefreshing: Boolean = false,
    val showSearchBar: Boolean = false,
    val isLoadingMore: Boolean = false,
    val paginationError: String? = null,
)

class CharacterListViewModel(
    private val getCharactersUseCase: GetCharactersUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CharacterListState())
    val state = _state.asStateFlow()
    private var loadJob: Job? = null

    fun toggleSearchBar() {
        _state.update { currentState ->
            currentState.copy(showSearchBar = !currentState.showSearchBar)
        }
    }

    fun onNameQueryChange(query: String) {
        _state.update { it.copy(nameQuery = query) }
        loadCharacters(forceRefresh = true)
    }

    fun loadCharacters(forceRefresh: Boolean = false) {
        if (!forceRefresh && _state.value.currentState is UiState.Success) return

        loadPage(pageNumber = 1, replaceCharacters = true)
    }

    fun loadNextPage() {
        val currentState = _state.value
        val nextPage =
            currentState.paginationMeta
                ?.takeIf { it.hasNextPage && it.nextPage != null && it.nextPage > it.currentPage }
                ?.nextPage ?: return
        if (currentState.isLoadingMore || currentState.currentState !is UiState.Success) return

        loadPage(pageNumber = nextPage, replaceCharacters = false)
    }

    private fun loadPage(
        pageNumber: Int,
        replaceCharacters: Boolean,
    ) {
        loadJob?.cancel()
        val query = _state.value.nameQuery
        _state.update { currentState ->
            if (replaceCharacters) {
                currentState.copy(
                    currentState = UiState.Loading,
                    paginationMeta = null,
                    isLoadingMore = false,
                    paginationError = null,
                )
            } else {
                currentState.copy(isLoadingMore = true, paginationError = null)
            }
        }
        loadJob =
            viewModelScope.launch {
                val characters =
                    getCharactersUseCase(
                        nameQuery = query,
                        pageNumber = pageNumber,
                        pageSize = PAGE_SIZE,
                    )
                characters.collect { result ->
                    _state.update { currentState ->
                        when (result) {
                            is Resource.Success -> {
                                currentState.withPageResult(
                                    characters = result.data.characters,
                                    paginationMeta = result.data.paginationMeta,
                                    replaceCharacters = replaceCharacters,
                                    isOffline = false,
                                )
                            }

                            is Resource.Offline -> {
                                currentState.withPageResult(
                                    characters = result.data.characters,
                                    paginationMeta = result.data.paginationMeta,
                                    replaceCharacters = replaceCharacters,
                                    isOffline = true,
                                )
                            }

                            is Resource.Error -> {
                                if (replaceCharacters) {
                                    currentState.copy(
                                        currentState = UiState.Error(message = result.message),
                                        isLoadingMore = false,
                                        paginationError = null,
                                    )
                                } else {
                                    currentState.copy(
                                        isLoadingMore = false,
                                        paginationError = result.message,
                                    )
                                }
                            }

                            Resource.Loading -> {
                                if (replaceCharacters) {
                                    currentState.copy(currentState = UiState.Loading)
                                } else {
                                    currentState.copy(isLoadingMore = true)
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun CharacterListState.withPageResult(
        characters: List<HpCharacter>,
        paginationMeta: PaginationMeta?,
        replaceCharacters: Boolean,
        isOffline: Boolean,
    ): CharacterListState {
        val existingCharacters =
            if (replaceCharacters) {
                emptyList()
            } else {
                (currentState as? UiState.Success)?.data.orEmpty()
            }
        val updatedCharacters =
            (existingCharacters + characters).distinctBy(HpCharacter::id)

        return copy(
            currentState =
                if (updatedCharacters.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(
                        data = updatedCharacters,
                        isOffline =
                            if (replaceCharacters) {
                                isOffline
                            } else {
                                (currentState as? UiState.Success)?.isOffline == true || isOffline
                            },
                    )
                },
            paginationMeta = paginationMeta,
            isLoadingMore = false,
            paginationError = null,
        )
    }

    private companion object {
        const val PAGE_SIZE = 10
    }
}
