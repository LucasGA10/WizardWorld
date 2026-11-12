package com.example.wizardworld.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavoriteIds(): Flow<Set<String>>

    suspend fun toggleFavorite(id: String)
}
