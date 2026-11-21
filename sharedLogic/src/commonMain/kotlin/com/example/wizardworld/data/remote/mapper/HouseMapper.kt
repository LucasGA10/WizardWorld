package com.example.wizardworld.data.remote.mapper

import com.example.wizardworld.data.remote.dto.HouseDto
import com.example.wizardworld.data.remote.dto.HouseHeadDto
import com.example.wizardworld.data.remote.dto.TraitDto
import com.example.wizardworld.domain.model.House
import com.example.wizardworld.domain.model.HouseHead
import com.example.wizardworld.domain.model.HouseListItem
import com.example.wizardworld.domain.model.Trait

fun HouseDto.toListItem(): HouseListItem = toDomain().toListItem()

fun House.toListItem(): HouseListItem =
    HouseListItem(
        id = id,
        name = name,
        houseColours = houseColours,
        founder = founder,
        animal = animal,
        element = element,
    )

fun HouseDto.toDomain(): House =
    House(
        id = id,
        name = name.orEmpty(),
        houseColours = houseColours.orEmpty(),
        founder = founder.orEmpty(),
        animal = animal.orEmpty(),
        element = element.orEmpty(),
        ghost = ghost.orEmpty(),
        commonRoom = commonRoom.orEmpty(),
        heads = heads.orEmpty().map { it.toDomain() },
        traits = traits.orEmpty().map { it.toDomain() },
    )

fun HouseHeadDto.toDomain(): HouseHead =
    HouseHead(
        id = id,
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
    )

fun TraitDto.toDomain(): Trait =
    Trait(
        id = id,
        name = name.orEmpty(),
    )
