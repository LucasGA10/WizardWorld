package com.example.wizardworld

import androidx.compose.ui.window.ComposeUIViewController
import com.example.wizardworld.di.sharedModule
import com.example.wizardworld.ui.screens.characters.CharacterListScreen
import com.example.wizardworld.ui.theme.WizardWorldTheme
import org.koin.core.context.startKoin

fun MainViewController() =
    run {
        startKoin {
            modules(sharedModule)
        }
        ComposeUIViewController {
            WizardWorldTheme {
                CharacterListScreen(onCharacterClick = {})
            }
        }
    }
