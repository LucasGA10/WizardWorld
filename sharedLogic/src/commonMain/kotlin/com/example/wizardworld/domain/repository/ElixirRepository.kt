package com.example.wizardworld.domain.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.Elixir
import kotlinx.coroutines.flow.Flow

interface ElixirRepository {
    fun getElixirs(
        name: String? = null,
        difficulty: String? = null,
        ingredient: String? = null,
        inventorFullName: String? = null,
        manufacturer: String? = null,
    ): Flow<Resource<List<Elixir>>>

    fun getElixir(id: String): Flow<Resource<Elixir>>
}
