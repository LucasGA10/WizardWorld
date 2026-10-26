package com.example.wizardworld.domain.repository

import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.model.NetworkResult

interface HouseRepository {
    suspend fun getHouses(): NetworkResult<List<HouseListItem>>

    suspend fun getHouse(id: String): NetworkResult<House>
}
