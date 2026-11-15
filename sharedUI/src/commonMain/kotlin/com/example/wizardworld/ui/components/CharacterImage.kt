package com.example.wizardworld.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import com.example.wizardworld.domain.model.HpCharacter

@Composable
fun CharacterImage(
    character: HpCharacter,
    imageHeight: Dp,
    placeholderSize: Dp,
    modifier: Modifier = Modifier,
) {
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
        modifier = modifier.fillMaxWidth().height(imageHeight),
        contentAlignment = Alignment.Center,
    ) {
        if (imageError) {
            Box(
                modifier =
                    Modifier
                        .size(placeholderSize)
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
                alignment = Alignment.TopCenter,
                onError = { imageError = true },
                modifier = Modifier.fillMaxWidth().height(imageHeight),
            )
        }
    }
}
