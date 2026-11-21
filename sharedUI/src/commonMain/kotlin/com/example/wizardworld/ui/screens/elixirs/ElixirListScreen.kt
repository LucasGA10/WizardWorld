
package com.example.wizardworld.ui.screens.elixirs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.elixirs.ElixirListViewModel
import com.example.wizardworld.ui.components.AppLogo
import com.example.wizardworld.ui.components.OfflineBanner
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElixirListScreen(onElixirClick: (String) -> Unit = {}) {
    val viewModel: ElixirListViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedElixir by remember { mutableStateOf<Elixir?>(null) }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (selectedElixir == null) {
                        AppLogo(
                            contentDescription = "WizardWorld",
                        )
                    }

                    Text(
                        text = selectedElixir?.name ?: "Elixires",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            },
            navigationIcon = {
                if (selectedElixir != null) {
                    IconButton(
                        onClick = { selectedElixir = null },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver al listado",
                        )
                    }
                }
            },
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            modifier = Modifier.shadow(4.dp),
        )

        OfflineBanner(
            visible = (state as? UiState.Success)?.isOffline == true,
        )

        val detail = selectedElixir

        if (detail != null) {
            ElixirDetailContent(elixir = detail)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp,
                            ),
                    placeholder = {
                        Text("Buscar elixir por nombre")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar elixires",
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
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
                            val filteredElixirs =
                                currentState.data.filter { elixir ->
                                    elixir.name.contains(
                                        searchQuery,
                                        ignoreCase = true,
                                    )
                                }

                            if (filteredElixirs.isEmpty()) {
                                EmptyElixirsContent(
                                    title = "No se encontraron elixires",
                                    message =
                                        if (searchQuery.isNotBlank()) {
                                            "Probá con otro nombre."
                                        } else {
                                            "No hay elixires disponibles."
                                        },
                                )
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement =
                                        Arrangement.spacedBy(12.dp),
                                    verticalArrangement =
                                        Arrangement.spacedBy(12.dp),
                                ) {
                                    items(
                                        items = filteredElixirs,
                                        key = { it.id },
                                    ) { elixir ->
                                        ElixirItemCard(
                                            elixir = elixir,
                                            onClick = {
                                                selectedElixir = elixir
                                                onElixirClick(elixir.id)
                                            },
                                        )
                                    }
                                }
                            }
                        }

                        UiState.Empty -> {
                            EmptyElixirsContent(
                                title = "No se encontraron elixires",
                                message = "No hay elixires disponibles.",
                            )
                        }

                        is UiState.Error -> {
                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                verticalArrangement =
                                    Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(24.dp),
                            ) {
                                Text(
                                    text = currentState.message,
                                    color = MaterialTheme.colorScheme.error,
                                )

                                TextButton(
                                    onClick = {
                                        viewModel.loadElixirs(
                                            forceRefresh = true,
                                        )
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
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "⚗",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Text(
                text = elixir.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            elixir.effect
                ?.takeIf { it.isNotBlank() }
                ?.let { effect ->
                    Text(
                        text = effect,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

            Text(
                text = "Dificultad: ${elixir.difficulty.orDisplayValue()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "Ver detalle",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ElixirDetailContent(elixir: Elixir) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "⚗",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )

                Text(
                    text = elixir.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )

                Text(
                    text = "Dificultad: ${elixir.difficulty.orDisplayValue()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        DetailSection(
            title = "Efecto",
            value = elixir.effect,
        )

        DetailSection(
            title = "Efectos secundarios",
            value = elixir.sideEffects,
        )

        DetailSection(
            title = "Características",
            value = elixir.characteristics,
        )

        DetailSection(
            title = "Tiempo de preparación",
            value = elixir.time,
        )

        DetailSection(
            title = "Fabricante",
            value = elixir.manufacturer,
        )

        DetailSection(
            title = "Ingredientes",
            value =
                elixir.ingredients
                    .joinToString("\n") { ingredient ->
                        "• ${ingredient.name}"
                    }
                    .ifBlank { null },
        )

        DetailSection(
            title = "Inventores",
            value =
                elixir.inventors
                    .joinToString("\n") { inventor ->
                        val name =
                            listOfNotNull(
                                inventor.firstName,
                                inventor.lastName,
                            ).joinToString(" ").trim()

                        if (name.isBlank()) {
                            "• Desconocido"
                        } else {
                            "• $name"
                        }
                    }
                    .ifBlank { null },
        )
    }
}

@Composable
private fun DetailSection(
    title: String,
    value: String?,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = value.orDisplayValue(),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun EmptyElixirsContent(
    title: String,
    message: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun String?.orDisplayValue(): String = this?.takeIf { it.isNotBlank() } ?: "Desconocido"
