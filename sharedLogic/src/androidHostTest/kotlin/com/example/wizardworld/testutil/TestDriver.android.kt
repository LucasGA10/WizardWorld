package com.example.wizardworld.testutil

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.wizardworld.db.WizardWorldDatabase

actual fun createTestDriver(): SqlDriver =
    JdbcSqliteDriver(
        url = JdbcSqliteDriver.IN_MEMORY,
        schema = WizardWorldDatabase.Schema,
    )
