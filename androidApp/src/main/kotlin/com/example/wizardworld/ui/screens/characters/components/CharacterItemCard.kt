package com.example.wizardworld.ui.screens.characters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wizardworld.domain.model.CharacterItem

@Composable
fun CharacterItemCard(
    character: CharacterItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val firstName = character.firstName
    val lastName = character.lastName

    Card(
        modifier = modifier,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CharacterAvatar(firstName = firstName, lastName = lastName)

            Column(modifier = Modifier.padding(start = 12.dp)) {
                if (firstName != null) {
                    Text(
                        text = firstName,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                if (lastName != null) {
                    Text(
                        text = lastName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun CharacterAvatar(
    firstName: String?,
    lastName: String?,
    modifier: Modifier = Modifier,
) {
    val initials =
        listOfNotNull(firstName?.firstOrNull(), lastName?.firstOrNull())
            .joinToString("")
            .ifBlank { "?" }
            .uppercase()

    Box(
        modifier =
            modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary,
        )
    }
}
