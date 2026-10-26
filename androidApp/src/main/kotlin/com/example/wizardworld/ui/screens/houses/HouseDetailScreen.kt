package com.example.wizardworld.ui.screens.houses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardworld.R
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.presentation.HouseDetailViewModel
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.components.LoadingState
import com.example.wizardworld.ui.components.OfflineBanner
import com.example.wizardworld.ui.house.houseAccentColor
import com.example.wizardworld.ui.house.houseOnAccentColor
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseDetailScreen(
    houseId: String,
    onBack: () -> Unit,
    viewModel: HouseDetailViewModel = koinViewModel(parameters = { parametersOf(houseId) }),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val title =
        (uiState as? UiState.Success)?.data?.name ?: stringResource(R.string.house_details)
    val accentName = (uiState as? UiState.Success)?.data?.name.orEmpty()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(text = title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = houseAccentColor(accentName.ifBlank { "gryffindor" }),
                        titleContentColor = houseOnAccentColor(accentName.ifBlank { "gryffindor" }),
                        navigationIconContentColor = houseOnAccentColor(accentName.ifBlank { "gryffindor" }),
                        scrolledContainerColor = houseAccentColor(accentName.ifBlank { "gryffindor" }),
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            when (val state = uiState) {
                UiState.Loading -> LoadingState()
                UiState.Empty ->
                    EmptyState(
                        title = stringResource(R.string.empty_house_detail_title),
                        message = stringResource(R.string.empty_house_detail_message),
                    )
                is UiState.Error ->
                    ErrorState(
                        message = state.message,
                        onRetry = viewModel::retry,
                    )
                is UiState.Success -> {
                    OfflineBanner(visible = state.isOffline)
                    HouseDetailContent(house = state.data)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HouseDetailContent(house: House) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = house.houseColours,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            AttributeCard(
                label = stringResource(R.string.attribute_founder),
                value = house.founder,
            )
            AttributeCard(
                label = stringResource(R.string.attribute_animal),
                value = house.animal,
            )
            AttributeCard(
                label = stringResource(R.string.attribute_colours),
                value = house.houseColours,
            )
            AttributeCard(
                label = stringResource(R.string.attribute_ghost),
                value = house.ghost,
            )
            AttributeCard(
                label = stringResource(R.string.attribute_element),
                value = house.element,
            )
            AttributeCard(
                label = stringResource(R.string.attribute_common_room),
                value = house.commonRoom,
            )
        }
        if (house.heads.isNotEmpty()) {
            Text(
                text = stringResource(R.string.heads_title),
                style = MaterialTheme.typography.titleLarge,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                house.heads.forEach { head ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(text = "${head.firstName} ${head.lastName}".trim()) },
                    )
                }
            }
        }
        if (house.traits.isNotEmpty()) {
            Text(
                text = stringResource(R.string.traits_title),
                style = MaterialTheme.typography.titleLarge,
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                house.traits.forEach { trait ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(text = trait.name) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AttributeCard(
    label: String,
    value: String,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(0.48f),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
