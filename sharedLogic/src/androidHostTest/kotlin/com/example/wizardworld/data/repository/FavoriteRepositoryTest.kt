package com.example.wizardworld.data.repository

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.wizardworld.data.local.createWizardWorldDatabase
import com.example.wizardworld.db.WizardWorldDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class FavoriteRepositoryTest {
    @Test
    fun databaseMigrationFromVersion3_createsFavoriteTable() {
        runBlocking {
            val driver = JdbcSqliteDriver(url = JdbcSqliteDriver.IN_MEMORY)

            WizardWorldDatabase.Schema.migrate(driver, oldVersion = 3, newVersion = 4)
            val repository = FavoriteRepositoryImpl(WizardWorldDatabase(driver))

            repository.toggleFavorite(HARRY_ID)

            assertEquals(setOf(HARRY_ID), repository.observeFavoriteIds().first())
        }
    }

    @Test
    fun toggleFavorite_addsFavoriteId() {
        runBlocking {
            val repository = createFavoriteRepository()

            repository.toggleFavorite(HARRY_ID)

            assertEquals(setOf(HARRY_ID), repository.observeFavoriteIds().first())
        }
    }

    @Test
    fun toggleFavorite_removesExistingFavoriteId() {
        runBlocking {
            val repository = createFavoriteRepository()
            repository.toggleFavorite(HARRY_ID)

            repository.toggleFavorite(HARRY_ID)

            assertEquals(emptySet(), repository.observeFavoriteIds().first())
        }
    }

    @Test
    fun toggleFavorite_persistsWhenRepositoryIsRecreated() {
        runBlocking {
            val database = createTestDatabase()
            FavoriteRepositoryImpl(database).toggleFavorite(HARRY_ID)

            val recreatedRepository = FavoriteRepositoryImpl(database)

            assertEquals(setOf(HARRY_ID), recreatedRepository.observeFavoriteIds().first())
        }
    }
}

private fun createFavoriteRepository(): FavoriteRepositoryImpl {
    return FavoriteRepositoryImpl(createTestDatabase())
}

private fun createTestDatabase(): WizardWorldDatabase {
    val driver =
        JdbcSqliteDriver(
            url = JdbcSqliteDriver.IN_MEMORY,
            schema = WizardWorldDatabase.Schema,
        )
    return createWizardWorldDatabase(driver)
}

private const val HARRY_ID = "harry-potter"
