package com.example.wizardworld

import android.app.Application
import com.example.wizardworld.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WizardWorldApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WizardWorldApplication)
            modules(sharedModule)
        }
    }
}
