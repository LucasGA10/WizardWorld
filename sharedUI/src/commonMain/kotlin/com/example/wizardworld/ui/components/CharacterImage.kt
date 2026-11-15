package com.example.wizardworld.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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

    Box(
        modifier = modifier.fillMaxWidth().height(imageHeight),
        contentAlignment = Alignment.Center,
    ) {
        if (imageError) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = character.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(placeholderSize),
            )
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
