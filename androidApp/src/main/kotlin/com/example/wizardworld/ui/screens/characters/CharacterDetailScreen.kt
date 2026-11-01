
package com.example.wizardworld.ui.screens.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.wizardworld.R
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.characters.CharacterDetailViewModel
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.components.LoadingState
import com.example.wizardworld.ui.components.OfflineBanner
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: String,
    onBack: () -> Unit,
    viewModel: CharacterDetailViewModel =
        koinViewModel(
            parameters = { parametersOf(characterId) },
        ),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.character_detail_title),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription =
                                stringResource(R.string.character_detail_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
        ) {
            when (val currentState = state.currentState) {
                UiState.Loading -> {
                    LoadingState()
                }

                is UiState.Success -> {
                    Column {
                        OfflineBanner(
                            visible = currentState.isOffline,
                        )

                        CharacterDetailContent(
                            character = currentState.data,
                        )
                    }
                }

                is UiState.Error -> {
                    ErrorState(
                        message = currentState.message,
                        onRetry = viewModel::loadCharacter,
                    )
                }

                UiState.Empty -> {
                    EmptyState(
                        title =
                            stringResource(
                                R.string.character_detail_not_found,
                            ),
                        message =
                            stringResource(
                                R.string.character_detail_not_found_message,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterDetailContent(character: HpCharacter) {
    val unknownMasculine =
        stringResource(R.string.character_detail_unknown_masculine)
    val unknownFeminine =
        stringResource(R.string.character_detail_unknown_feminine)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CharacterImage(character)

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_house),
                value = character.house.orEmpty().ifBlank {
                    unknownFeminine
                },
                modifier = Modifier.weight(1f),
            )

            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_species),
                value = character.species.orEmpty().ifBlank {
                    unknownFeminine
                },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_gender),
                value = character.gender.orEmpty().ifBlank {
                    unknownMasculine
                },
                modifier = Modifier.weight(1f),
            )

            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_birth),
                value = character.dateOfBirth.orEmpty().ifBlank {
                    unknownMasculine
                },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_patronus),
                value = character.patronus.orEmpty().ifBlank {
                    unknownMasculine
                },
                modifier = Modifier.weight(1f),
            )

            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_status),
                value =
                    if (character.isAlive) {
                        stringResource(R.string.character_detail_alive)
                    } else {
                        stringResource(R.string.character_detail_deceased)
                    },
                modifier = Modifier.weight(1f),
            )
        }

        CharacterAttributeCard(
            title = stringResource(R.string.character_detail_actor),
            value = character.actor.orEmpty().ifBlank {
                unknownMasculine
            },
            modifier = Modifier.fillMaxWidth(),
        )

        character.wand?.let { wand ->
            Text(
                text = stringResource(R.string.character_detail_wand),
                style = MaterialTheme.typography.titleLarge,
            )

            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_wood),
                value = wand.wood.orEmpty().ifBlank {
                    unknownFeminine
                },
                modifier = Modifier.fillMaxWidth(),
            )

            CharacterAttributeCard(
                title = stringResource(R.string.character_detail_core),
                value = wand.core.orEmpty().ifBlank {
                    unknownMasculine
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CharacterImage(character: HpCharacter) {
    var imageError by remember(character.imageUrl) {
        mutableStateOf(character.imageUrl.isNullOrBlank())
    }

    val initials =
        character.name
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .ifBlank { "?" }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(260.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (imageError) {
            Box(
                modifier =
                    Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                contentScale = ContentScale.Crop,
                onError = {
                    imageError = true
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(260.dp),
            )
        }
    }
}

@Composable
private fun CharacterAttributeCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp,
            ),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
