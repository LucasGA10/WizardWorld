package com.example.wizardworld.data.repository

import com.example.wizardworld.data.local.HouseLocalDataSource
import com.example.wizardworld.data.remote.dto.HouseDto
import com.example.wizardworld.data.remote.mapper.toDomain
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.model.NetworkResult
import com.example.wizardworld.domain.repository.HouseRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlin.coroutines.cancellation.CancellationException

class HouseRepositoryImpl(
    private val httpClient: HttpClient,
    private val localDataSource: HouseLocalDataSource,
) : HouseRepository {
    override suspend fun getHouses(): NetworkResult<List<HouseListItem>> {
        return try {
            val houses = httpClient.get("Houses").body<List<HouseDto>>().map { it.toDomain() }
            localDataSource.replaceHouses(houses)
            NetworkResult.Success(localDataSource.getHouses())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val cached = localDataSource.getHouses()
            if (cached.isNotEmpty()) {
                NetworkResult.Offline(cached)
            } else {
                NetworkResult.Error(exception)
            }
        }
    }

    override suspend fun getHouse(id: String): NetworkResult<House> {
        return try {
            val house = httpClient.get("Houses/$id").body<HouseDto>().toDomain()
            localDataSource.replaceHouse(house)
            NetworkResult.Success(localDataSource.getHouse(id) ?: house)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            val cached = localDataSource.getHouse(id)
            if (cached != null) {
                NetworkResult.Offline(cached)
            } else {
                NetworkResult.Error(exception)
            }
        }
    }
}
