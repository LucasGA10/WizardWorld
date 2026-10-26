package com.example.wizardworld.ui.house

import androidx.compose.ui.graphics.Color
import com.example.wizardworld.ui.theme.GryffindorRed
import com.example.wizardworld.ui.theme.HufflepuffGold
import com.example.wizardworld.ui.theme.RavenclawBlue
import com.example.wizardworld.ui.theme.SlytherinGreen

fun houseAccentColor(name: String): Color =
    when (name.lowercase()) {
        "gryffindor" -> GryffindorRed
        "slytherin" -> SlytherinGreen
        "ravenclaw" -> RavenclawBlue
        "hufflepuff" -> HufflepuffGold
        else -> GryffindorRed
    }

fun houseOnAccentColor(name: String): Color =
    when (name.lowercase()) {
        "hufflepuff" -> Color(0xFF372E13)
        else -> Color.White
    }
