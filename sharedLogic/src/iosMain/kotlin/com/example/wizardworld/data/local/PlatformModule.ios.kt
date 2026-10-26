package com.example.wizardworld.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.wizardworld.db.WizardWorldDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module =
    module {
        single<SqlDriver> {
            NativeSqliteDriver(
                schema = WizardWorldDatabase.Schema,
                name = "wizard_world.db",
            )
        }
    }
