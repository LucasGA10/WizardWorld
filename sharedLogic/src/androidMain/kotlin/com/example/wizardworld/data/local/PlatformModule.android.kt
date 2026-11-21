package com.example.wizardworld.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.wizardworld.db.WizardWorldDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<SqlDriver> {
            AndroidSqliteDriver(
                schema = WizardWorldDatabase.Schema,
                context = get(),
                name = "wizard_world.db",
            )
        }
    }
