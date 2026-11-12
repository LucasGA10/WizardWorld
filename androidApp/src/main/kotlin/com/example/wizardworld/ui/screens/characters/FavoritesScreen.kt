package com.example.wizardworld.ui.screens.characters

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardworld.R
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.characters.FavoritesViewModel
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.LoadingState
import com.example.wizardworld.ui.screens.characters.components.CharacterItemCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesScreen(
    onCharacterClick: (String) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = stringResource(R.string.nav_favorites)) },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
        )

        when (val currentState = state) {
            UiState.Loading -> {
                LoadingState()
            }

            UiState.Empty -> {
                EmptyState(
                    title = stringResource(R.string.empty_favorites_title),
                    message = stringResource(R.string.empty_favorites_message),
                    icon = Icons.Filled.Favorite,
                )
            }

            is UiState.Error -> {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            is UiState.Success -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(currentState.data, key = { it.id }) { character ->
                        CharacterItemCard(
                            character = character,
                            onClick = { onCharacterClick(character.id) },
                        )
                    }
                }
            }
        }
    }
}
