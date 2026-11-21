package com.example.wizardworld.domain.usecases

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.repository.HouseRepository

class GetHousesUseCase(
    private val repository: HouseRepository,
) {
    suspend operator fun invoke(): Resource<List<HouseListItem>> = repository.getHouses()
}
