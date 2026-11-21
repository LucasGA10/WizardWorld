package com.example.wizardworld.testutil

import app.cash.sqldelight.db.SqlDriver
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.db.WizardWorldDatabase

/** Driver SQLite en memoria; cada plataforma provee su implementación. */
expect fun createTestDriver(): SqlDriver

fun createTestDatabase(): WizardWorldDatabase = createWizardWorldDatabase(createTestDriver())
