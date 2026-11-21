
package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.Elixir
import com.example.wizardworld.domain.repository.ElixirRepository
import kotlinx.coroutines.flow.Flow

class GetElixirsUseCase(
    private val repository: ElixirRepository,
) {
    operator fun invoke(
        name: String? = null,
        difficulty: String? = null,
        ingredient: String? = null,
        inventorFullName: String? = null,
        manufacturer: String? = null,
    ): Flow<Resource<List<Elixir>>> =
        repository.getElixirs(
            name = name,
            difficulty = difficulty,
            ingredient = ingredient,
            inventorFullName = inventorFullName,
            manufacturer = manufacturer,
        )
}
