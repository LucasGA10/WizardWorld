package com.example.wizardworld.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = Gold80,
        onPrimary = Color.White,
        secondary = Color(0xFFD0B9A1),
        tertiary = Sage80,
        background = Color(0xFF140C1C),
        surface = Color(0xFF1E1428),
        surfaceContainerHigh = Color(0xFF2A1C38),
        surfaceVariant = Color(0xFF423154),
        onSurfaceVariant = Color(0xFFD4C4B4),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = GryffindorRed,
        onPrimary = Color.White,
        secondary = Gold40,
        tertiary = Forest40,
        background = Color(0xFFFFFBF3),
        surface = Color(0xFFF7F1E5),
        surfaceContainerHigh = Color(0xFFF0E6D4),
        surfaceVariant = Color(0xFFF0E6D4),
        onSurfaceVariant = Color(0xFF4F4539),
    )

@Composable
fun WizardWorldTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> {
                DarkColorScheme
            }

            else -> {
                LightColorScheme
            }
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
