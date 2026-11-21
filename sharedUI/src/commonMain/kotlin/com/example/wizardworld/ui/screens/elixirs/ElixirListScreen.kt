
package com.example.wizardworld.ui.screens.elixirs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.elixirs.ElixirListViewModel
import com.example.wizardworld.ui.components.OfflineBanner
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElixirListScreen(onElixirClick: (String) -> Unit = {}) {
    val viewModel: ElixirListViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadElixirs()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        TopAppBar(
            title = {
                Text("Elixires")
            },
        )

        OfflineBanner(
            visible = (state as? UiState.Success)?.isOffline == true,
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            when (val currentState = state) {
                UiState.Loading -> {
                    CircularProgressIndicator()
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = currentState.data,
                            key = { it.id },
                        ) { elixir ->
                            ElixirItemCard(
                                elixir = elixir,
                                onClick = {
                                    onElixirClick(elixir.id)
                                },
                            )
                        }
                    }
                }

                UiState.Empty -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(24.dp),
                    ) {
                        Text(
                            text = "No se encontraron elixires",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "No hay elixires disponibles para mostrar.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                is UiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(24.dp),
                    ) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                        )

                        TextButton(
                            onClick = {
                                viewModel.loadElixirs(forceRefresh = true)
                            },
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ElixirItemCard(
    elixir: Elixir,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = elixir.name,
                style = MaterialTheme.typography.titleMedium,
            )

            elixir.effect?.takeIf { it.isNotBlank() }?.let { effect ->
                Text(
                    text = effect,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            elixir.difficulty?.takeIf { it.isNotBlank() }?.let { difficulty ->
                Text(
                    text = "Dificultad: $difficulty",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
