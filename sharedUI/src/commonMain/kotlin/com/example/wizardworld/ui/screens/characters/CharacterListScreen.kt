package com.example.wizardworld.ui.screens.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.characters.CharacterListViewModel
import com.example.wizardworld.ui.components.AppLogo
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.components.OfflineBanner
import com.example.wizardworld.ui.screens.characters.components.CharacterItemCard
import com.example.wizardworld.ui.screens.characters.components.CharacterListLoading
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import wizardworld.sharedui.generated.resources.Res
import wizardworld.sharedui.generated.resources.app_logo_cd
import wizardworld.sharedui.generated.resources.character_list_title
import wizardworld.sharedui.generated.resources.empty_characters_message
import wizardworld.sharedui.generated.resources.empty_characters_title
import wizardworld.sharedui.generated.resources.pagination_load_more
import wizardworld.sharedui.generated.resources.pagination_retry
import wizardworld.sharedui.generated.resources.search_characters
import wizardworld.sharedui.generated.resources.search_characters_cd

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(onCharacterClick: (String) -> Unit) {
    val viewModel: CharacterListViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val hasNextPage =
        state.paginationMeta?.let { metadata ->
            metadata.hasNextPage &&
                metadata.nextPage?.let { it > metadata.currentPage } == true
        } == true

    LaunchedEffect(Unit) {
        viewModel.loadCharacters()
    }

    LaunchedEffect(state.nameQuery) {
        gridState.scrollToItem(0)
    }

    LaunchedEffect(gridState) {
        combine(
            snapshotFlow {
                gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            },
            viewModel.state,
        ) { lastVisibleIndex, listState ->
            val metadata = listState.paginationMeta
            PaginationTrigger(
                lastVisibleIndex = lastVisibleIndex,
                characterCount = (listState.currentState as? UiState.Success)?.data?.size ?: 0,
                hasNextPage =
                    metadata?.let {
                        it.hasNextPage && it.nextPage?.let { nextPage -> nextPage > it.currentPage } == true
                    } == true,
                isLoadingMore = listState.isLoadingMore,
                paginationError = listState.paginationError,
            )
        }.distinctUntilChanged()
            .collect { trigger ->
                val prefetchIndex = (trigger.characterCount - PREFETCH_ITEM_COUNT).coerceAtLeast(0)
                if (
                    trigger.hasNextPage &&
                    trigger.lastVisibleIndex >= prefetchIndex &&
                    !trigger.isLoadingMore &&
                    trigger.paginationError == null
                ) {
                    viewModel.loadNextPage()
                }
            }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AppLogo(
                        contentDescription = stringResource(Res.string.app_logo_cd),
                    )
                    Text(text = stringResource(Res.string.character_list_title))
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            modifier = Modifier.shadow(elevation = 4.dp),
        )

        OfflineBanner(visible = (state.currentState as? UiState.Success)?.isOffline == true)

        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.nameQuery.orEmpty(),
                    onQueryChange = viewModel::onNameQueryChange,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text(text = stringResource(Res.string.search_characters)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(Res.string.search_characters_cd),
                        )
                    },
                )
            },
            expanded = false,
            onExpandedChange = {},
            windowInsets = WindowInsets(0, 0, 0, 0),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {}

        Box(modifier = Modifier.weight(1f)) {
            when (val currentState = state.currentState) {
                UiState.Loading -> {
                    CharacterListLoading()
                }

                is UiState.Success -> {
                    if (currentState.data.isNotEmpty()) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            items(
                                count = currentState.data.size,
                                key = { index -> currentState.data[index].id },
                            ) { index ->
                                CharacterItemCard(
                                    character = currentState.data[index],
                                    onClick = {
                                        onCharacterClick(currentState.data[index].id)
                                    },
                                )
                            }
                            if (
                                state.isLoadingMore ||
                                state.paginationError != null ||
                                hasNextPage
                            ) {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    when {
                                        state.isLoadingMore -> {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                )
                                            }
                                        }

                                        state.paginationError != null -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .padding(8.dp),
                                            ) {
                                                Text(
                                                    text = state.paginationError.orEmpty(),
                                                    color = MaterialTheme.colorScheme.error,
                                                )
                                                TextButton(onClick = viewModel::loadNextPage) {
                                                    Text(stringResource(Res.string.pagination_retry))
                                                }
                                            }
                                        }

                                        else -> {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                            ) {
                                                TextButton(onClick = viewModel::loadNextPage) {
                                                    Text(stringResource(Res.string.pagination_load_more))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = currentState.message,
                        onRetry = viewModel::loadCharacters,
                    )
                }

                UiState.Empty -> {
                    EmptyState(
                        title = stringResource(Res.string.empty_characters_title),
                        message = stringResource(Res.string.empty_characters_message),
                    )
                }
            }
        }
    }
}

private const val PREFETCH_ITEM_COUNT = 4

private data class PaginationTrigger(
    val lastVisibleIndex: Int,
    val characterCount: Int,
    val hasNextPage: Boolean,
    val isLoadingMore: Boolean,
    val paginationError: String?,
)
