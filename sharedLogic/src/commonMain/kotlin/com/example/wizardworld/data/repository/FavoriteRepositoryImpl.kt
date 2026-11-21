package com.example.wizardworld.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoriteRepositoryImpl(
    private val database: WizardWorldDatabase,
) : FavoriteRepository {
    override fun observeFavoriteIds(): Flow<Set<String>> =
        database.favoriteQueries
            .selectAllFavoriteIds()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { favoriteIds -> favoriteIds.toSet() }

    override suspend fun toggleFavorite(id: String) {
        withContext(Dispatchers.Default) {
            database.transaction {
                if (database.favoriteQueries.isFavorite(id).executeAsOne()) {
                    database.favoriteQueries.deleteFavorite(id)
                } else {
                    database.favoriteQueries.insertFavorite(id)
                }
            }
        }
    }
}
