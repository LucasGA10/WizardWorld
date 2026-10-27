package com.example.wizardworld.di

import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.HouseLocalDataSource
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.data.local.platformModule
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.provideHttpClient
import com.example.wizardworld.data.repository.HouseRepositoryImpl
import com.example.wizardworld.data.repository.WizardWorldRepositoryImpl
import com.example.wizardworld.domain.repository.HouseRepository
import com.example.wizardworld.domain.repository.WizardWorldRepository
import com.example.wizardworld.presentation.HouseDetailViewModel
import com.example.wizardworld.presentation.HouseListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule =
    module {
        includes(platformModule)
        single { provideHttpClient() }
        single { WizardWorldApi(get()) }
        single { createWizardWorldDatabase(get()) }
        single { HouseLocalDataSource(get()) }
        single<HouseRepository> { HouseRepositoryImpl(get(), get()) }
        single { CharacterLocalDataSource(get()) }
        single<WizardWorldRepository> { WizardWorldRepositoryImpl(get(), get()) }
        viewModelOf(::HouseListViewModel)
        viewModel { parameters ->
            HouseDetailViewModel(
                houseId = parameters.get(),
                houseRepository = get(),
            )
        }
    }
