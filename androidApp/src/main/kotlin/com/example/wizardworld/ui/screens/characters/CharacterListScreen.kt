package com.example.wizardworld.ui.screens.characters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wizardworld.R
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.characters.CharacterListViewModel
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.screens.characters.components.CharacterItemCard
import com.example.wizardworld.ui.screens.characters.components.CharacterListLoading
import org.koin.androidx.compose.koinViewModel

@Composable
fun CharacterListScreen(viewModel: CharacterListViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCharacters()
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
                    Image(
                        painter = painterResource(R.drawable.ic_app_logo),
                        contentDescription = stringResource(R.string.app_logo_cd),
                        modifier = Modifier.size(40.dp),
                    )
                    Text(text = stringResource(R.string.character_list_title))
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            modifier = Modifier.shadow(elevation = 4.dp),
        )

        AnimatedContent(
            targetState = state.currentState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
            },
        ) { targetState ->
            when (targetState) {
                UiState.Loading -> {
                    CharacterListLoading()
                }

                is UiState.Success -> {
                    if (targetState.data.isNotEmpty()) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            items(targetState.data.size) { index ->
                                CharacterItemCard(
                                    character = targetState.data[index],
                                    onClick = { /*TODO: CharacterDetailScreen*/ },
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = targetState.message,
                        onRetry = viewModel::loadCharacters,
                    )
                }

                UiState.Empty -> {
                    EmptyState(
                        title = "Magos no encontrados",
                        message = "No se encontraron magos en la lista",
                    )
                }
            }
        }
    }
}
