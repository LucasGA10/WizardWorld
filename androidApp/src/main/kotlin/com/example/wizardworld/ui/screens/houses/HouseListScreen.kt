package com.example.wizardworld.ui.screens.houses

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardworld.R
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.presentation.houses.HouseListViewModel
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.components.LoadingState
import com.example.wizardworld.ui.components.OfflineBanner
import com.example.wizardworld.ui.components.WizardPullToRefresh
import com.example.wizardworld.ui.components.WizardSearchBar
import com.example.wizardworld.ui.components.WizardTopBar
import com.example.wizardworld.ui.screens.houses.components.houseAccentColor
import com.example.wizardworld.ui.screens.houses.components.houseOnAccentColor
import org.koin.androidx.compose.koinViewModel

@Composable
fun HouseListScreen(
    onHouseClick: (String) -> Unit,
    viewModel: HouseListViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    var showSearchBar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            WizardTopBar(
                title = {
                    AnimatedContent(
                        targetState = showSearchBar,
                        transitionSpec = {
                            fadeIn(
                                animationSpec = tween(300),
                            ) togetherWith
                                    fadeOut(
                                        animationSpec = tween(0),
                                    )
                        },
                    ) { isSearch ->
                        if (isSearch) {
                            WizardSearchBar(
                                searchQuery = searchQuery,
                                placeholder = stringResource(R.string.search_houses),
                                onSearchQueryChange = viewModel::onSearchQueryChange,
                                onCloseSearch = { showSearchBar = false },
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ic_app_logo),
                                    contentDescription = stringResource(R.string.houses_title),
                                    modifier = Modifier.size(40.dp),
                                )
                                Text(text = stringResource(R.string.houses_title),)
                            }
                        }
                    }
                },
                actions = {
                    if (!showSearchBar){
                        IconButton(
                            onClick = { showSearchBar = true },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_houses_cd),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            OfflineBanner(visible = isOffline || (uiState as? UiState.Success)?.isOffline == true)

            WizardPullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh,
                modifier = Modifier.fillMaxSize(),
            ) {
                when (val state = uiState) {
                    UiState.Loading -> LoadingState()
                    UiState.Empty ->
                        EmptyState(
                            title = stringResource(R.string.empty_houses_title),
                            message = stringResource(R.string.empty_houses_message),
                        )
                    is UiState.Error ->
                        ErrorState(
                            message = state.message,
                            onRetry = viewModel::retry,
                        )
                    is UiState.Success ->
                        HouseList(
                            houses = state.data,
                            onHouseClick = onHouseClick,
                        )
                }
            }
        }
    }
}

@Composable
private fun HouseList(
    houses: List<HouseListItem>,
    onHouseClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(houses, key = { it.id }) { house ->
            HouseCard(
                house = house,
                onClick = { onHouseClick(house.id) },
            )
        }
    }
}

@Composable
private fun HouseCard(
    house: HouseListItem,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(houseAccentColor(house.name)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = house.name.take(1),
                    style = MaterialTheme.typography.titleLarge,
                    color = houseOnAccentColor(house.name),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = house.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.house_animal_label, house.animal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.house_founder_label, house.founder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
