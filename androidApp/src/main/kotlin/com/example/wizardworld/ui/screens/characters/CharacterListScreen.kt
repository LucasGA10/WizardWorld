package com.example.wizardworld.ui.screens.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.characters.CharacterListViewModel
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.components.LoadingState
import com.example.wizardworld.ui.screens.characters.components.CharacterItemCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterListScreen(viewModel: CharacterListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val currentState = state.currentState) {
            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Success -> {
                if (currentState.data.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                    ) {
                        items(currentState.data.size) { index ->
                            CharacterItemCard(character = currentState.data[index])
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
                    title = "No data",
                    message = "No data available",
                )
            }
        }
    }
}
