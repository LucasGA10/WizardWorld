package com.example.wizardworld.data.local

import app.cash.sqldelight.db.SqlDriver
import com.example.wizardworld.db.WizardWorldDatabase

fun createWizardWorldDatabase(driver: SqlDriver): WizardWorldDatabase {
    driver.execute(null, "PRAGMA foreign_keys = ON;", 0)
    return WizardWorldDatabase(driver)
}
