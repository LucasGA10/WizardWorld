package com.example.wizardworld.domain.repository

import com.example.wizardworld.core.Resource
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.model.HouseListItem

interface HouseRepository {
    suspend fun getHouses(): Resource<List<HouseListItem>>

    suspend fun getHouse(id: String): Resource<House>
}
