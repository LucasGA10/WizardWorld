package com.example.wizardworld.ui.screens.characters.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CharacterDetailLoading(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "characterDetailShimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 900, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "characterDetailShimmerAlpha",
    )
    val shimmerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SkeletonBlock(
            color = shimmerColor,
            modifier = Modifier.fillMaxWidth().height(260.dp),
            shape = RoundedCornerShape(16.dp),
        )
        SkeletonBlock(
            color = shimmerColor,
            modifier = Modifier.fillMaxWidth(0.65f).height(32.dp),
        )
        repeat(3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(2) {
                    SkeletonBlock(
                        color = shimmerColor,
                        modifier = Modifier.weight(1f).height(82.dp),
                    )
                }
            }
        }
        SkeletonBlock(
            color = shimmerColor,
            modifier = Modifier.fillMaxWidth().height(82.dp),
        )
    }
}

@Composable
private fun SkeletonBlock(
    color: Color,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .background(color),
    )
}
