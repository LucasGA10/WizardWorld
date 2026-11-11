package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.repository.HouseRepository

class GetHouseUseCase(
    private val repository: HouseRepository,
) {
    suspend operator fun invoke(id: String): Resource<House> = repository.getHouse(id)
}
