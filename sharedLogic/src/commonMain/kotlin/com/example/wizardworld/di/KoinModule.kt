package com.example.wizardworld.di

import com.example.wizardworld.data.local.CharacterLocalDataSource
import com.example.wizardworld.data.local.HouseLocalDataSource
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.data.local.platformModule
import com.example.wizardworld.data.remote.HpApi
import com.example.wizardworld.data.remote.WizardWorldApi
import com.example.wizardworld.data.remote.provideHpHttpClient
import com.example.wizardworld.data.remote.provideWizardWorldHttpClient
import com.example.wizardworld.data.repository.CharacterRepositoryImpl
import com.example.wizardworld.data.repository.HouseRepositoryImpl
import com.example.wizardworld.domain.repository.CharacterRepository
import com.example.wizardworld.domain.repository.HouseRepository
import com.example.wizardworld.domain.usecases.GetCharacterDetailUseCase
import com.example.wizardworld.domain.usecases.GetCharactersUseCase
import com.example.wizardworld.presentation.HouseDetailViewModel
import com.example.wizardworld.presentation.HouseListViewModel
import com.example.wizardworld.presentation.characters.CharacterListViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule =
    module {
        includes(platformModule)

        single(named("WizardWorldHttpClient")) { provideWizardWorldHttpClient() }
        single(named("HpHttpClient")) { provideHpHttpClient() }

        single { WizardWorldApi(client = get(named("WizardWorldHttpClient"))) }
        single { HpApi(client = get(named("HpHttpClient"))) }

        single { createWizardWorldDatabase(get()) }
        single { HouseLocalDataSource(get()) }
        single<HouseRepository> {
            HouseRepositoryImpl(
                httpClient = get(named("WizardWorldHttpClient")),
                localDataSource = get(),
            )
        }
        singleOf(::CharacterLocalDataSource)
        singleOf(::CharacterRepositoryImpl) bind CharacterRepository::class
        factoryOf(::GetCharactersUseCase)
        factoryOf(::GetCharacterDetailUseCase)
        viewModelOf(::HouseListViewModel)
        viewModel { parameters ->
            HouseDetailViewModel(
                houseId = parameters.get(),
                houseRepository = get(),
            )
        }
        viewModelOf(::CharacterListViewModel)
    }
