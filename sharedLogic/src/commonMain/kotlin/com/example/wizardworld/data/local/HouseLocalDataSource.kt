package com.example.wizardworld.data.local

import com.example.wizardworld.db.HouseDetailEntity
import com.example.wizardworld.db.HouseEntity
import com.example.wizardworld.db.WizardWorldDatabase
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.model.HouseHead
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.model.Trait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HouseLocalDataSource(
    private val database: WizardWorldDatabase,
) {
    private val queries = database.houseQueries

    suspend fun getHouses(): List<HouseListItem> =
        withContext(Dispatchers.Default) {
            queries.selectAllHouses().executeAsList().map { it.toListItem() }
        }

    suspend fun getHouse(id: String): House? =
        withContext(Dispatchers.Default) {
            val detail = queries.selectHouseDetailById(id).executeAsOneOrNull() ?: return@withContext null
            val heads =
                queries.selectHeadsByHouseId(id).executeAsList().map { head ->
                    HouseHead(
                        id = head.id,
                        firstName = head.firstName,
                        lastName = head.lastName,
                    )
                }
            val traits =
                queries.selectTraitsByHouseId(id).executeAsList().map { trait ->
                    Trait(
                        id = trait.id,
                        name = trait.name,
                    )
                }
            detail.toDomain(heads, traits)
        }

    suspend fun replaceHouses(houses: List<House>) {
        withContext(Dispatchers.Default) {
            database.transaction {
                queries.deleteAllHouseHeads()
                queries.deleteAllHouseTraits()
                queries.deleteAllHouseDetails()
                queries.deleteAllHouses()
                houses.forEach { house ->
                    persistHouse(house)
                }
            }
        }
    }

    suspend fun replaceHouse(house: House) {
        withContext(Dispatchers.Default) {
            database.transaction {
                persistHouse(house)
            }
        }
    }

    private fun persistHouse(house: House) {
        queries.insertHouse(
            id = house.id,
            name = house.name,
            houseColours = house.houseColours,
            founder = house.founder,
            animal = house.animal,
            element = house.element,
        )
        queries.insertHouseDetail(
            id = house.id,
            name = house.name,
            houseColours = house.houseColours,
            founder = house.founder,
            animal = house.animal,
            element = house.element,
            ghost = house.ghost,
            commonRoom = house.commonRoom,
        )
        queries.deleteHeadsByHouseId(house.id)
        queries.deleteTraitsByHouseId(house.id)
        house.heads.forEach { head ->
            queries.insertHouseHead(
                id = head.id,
                houseId = house.id,
                firstName = head.firstName,
                lastName = head.lastName,
            )
        }
        house.traits.forEach { trait ->
            queries.insertHouseTrait(
                id = trait.id,
                houseId = house.id,
                name = trait.name,
            )
        }
    }
}

private fun HouseEntity.toListItem(): HouseListItem =
    HouseListItem(
        id = id,
        name = name,
        houseColours = houseColours,
        founder = founder,
        animal = animal,
        element = element,
    )

private fun HouseDetailEntity.toDomain(
    heads: List<HouseHead>,
    traits: List<Trait>,
): House =
    House(
        id = id,
        name = name,
        houseColours = houseColours,
        founder = founder,
        animal = animal,
        element = element,
        ghost = ghost,
        commonRoom = commonRoom,
        heads = heads,
        traits = traits,
    )
