package com.example.wizardworld.testutil

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.inMemoryDriver
import com.example.wizardworld.db.WizardWorldDatabase

actual fun createTestDriver(): SqlDriver = inMemoryDriver(WizardWorldDatabase.Schema)
