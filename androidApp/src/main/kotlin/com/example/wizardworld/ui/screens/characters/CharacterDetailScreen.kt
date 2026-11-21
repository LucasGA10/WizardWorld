
package com.example.wizardworld.ui.screens.characters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wizardworld.R
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.presentation.UiState
import com.example.wizardworld.presentation.characters.CharacterDetailViewModel
import com.example.wizardworld.ui.components.CharacterImage
import com.example.wizardworld.ui.components.EmptyState
import com.example.wizardworld.ui.components.ErrorState
import com.example.wizardworld.ui.components.OfflineBanner
import com.example.wizardworld.ui.screens.characters.components.CharacterAttributeCard
import com.example.wizardworld.ui.screens.characters.components.CharacterDetailLoading
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
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector =
                                if (state.isFavorite) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                            contentDescription =
                                stringResource(
                                    if (state.isFavorite) {
                                        R.string.remove_character_favorite
                                    } else {
                                        R.string.add_character_favorite
                                    },
                                ),
                            tint =
                                if (state.isFavorite) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
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
            AnimatedContent(
                targetState = state.currentState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "characterDetailState",
            ) { currentState ->
                when (currentState) {
                    UiState.Loading -> {
                        CharacterDetailLoading()
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
}

@Composable
private fun CharacterDetailContent(character: HpCharacter) {
    val unknownMasculine =
        stringResource(R.string.character_detail_unknown_masculine)
    val unknownFeminine =
        stringResource(R.string.character_detail_unknown_feminine)
    val uriHandler = LocalUriHandler.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CharacterImage(
            character = character,
            imageHeight = 260.dp,
            placeholderSize = 150.dp,
        )

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

        CharacterAttributesRow(
            first =
                character.animagus
                    ?.takeIf(String::isNotBlank)
                    ?.let {
                        stringResource(R.string.character_detail_animagus) to it
                    },
            second =
                character.boggart
                    ?.takeIf(String::isNotBlank)
                    ?.let {
                        stringResource(R.string.character_detail_boggart) to it
                    },
        )

        CharacterAttributesRow(
            first =
                character.titles
                    .filter(String::isNotBlank)
                    .takeIf(List<String>::isNotEmpty)
                    ?.let {
                        stringResource(R.string.character_detail_titles) to it.joinToString("\n")
                    },
            second =
                character.jobs
                    .filter(String::isNotBlank)
                    .takeIf(List<String>::isNotEmpty)
                    ?.let {
                        stringResource(R.string.character_detail_jobs) to it.joinToString("\n")
                    },
        )

        CharacterAttributesRow(
            first =
                character.familyMembers
                    .filter(String::isNotBlank)
                    .takeIf(List<String>::isNotEmpty)
                    ?.let {
                        stringResource(R.string.character_detail_family_members) to
                            it.joinToString("\n")
                    },
            second =
                character.romances
                    .filter(String::isNotBlank)
                    .takeIf(List<String>::isNotEmpty)
                    ?.let {
                        stringResource(R.string.character_detail_romances) to
                            it.joinToString("\n")
                    },
        )

        if (character.wands.isNotEmpty()) {
            Text(
                text = stringResource(R.string.character_detail_wand),
                style = MaterialTheme.typography.titleLarge,
            )

            character.wands.forEach { wand ->
                CharacterAttributeCard(
                    title = stringResource(R.string.character_detail_wand),
                    value = wand,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        character.wikiUrl
            ?.takeIf(String::isNotBlank)
            ?.let { wikiUrl ->
                OutlinedButton(
                    onClick = { uriHandler.openUri(wikiUrl) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.character_detail_open_wiki))
                }
            }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CharacterAttributesRow(
    first: Pair<String, String>?,
    second: Pair<String, String>?,
) {
    if (first == null && second == null) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        first?.let { (title, value) ->
            CharacterAttributeCard(
                title = title,
                value = value,
                modifier =
                    if (second == null) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.weight(1f)
                    },
            )
        }

        second?.let { (title, value) ->
            CharacterAttributeCard(
                title = title,
                value = value,
                modifier =
                    if (first == null) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.weight(1f)
                    },
            )
        }
    }
}
