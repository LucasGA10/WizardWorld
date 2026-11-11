package com.example.wizardworld.data.remote.mapper

import com.example.wizardworld.data.remote.dto.HpCharacterDto
import com.example.wizardworld.data.remote.dto.PotterDbMetaDto
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.PaginationMeta

fun HpCharacterDto.toDomain(): HpCharacter {
    val attr = attributes

    return HpCharacter(
        id = id,
        name = attr.name,
        alternateNames = attr.aliasNames,
        species = attr.species.takeIf { !it.isNullOrBlank() },
        gender = attr.gender.takeIf { !it.isNullOrBlank() },
        house = attr.house.takeIf { !it.isNullOrBlank() },
        dateOfBirth = attr.born.takeIf { !it.isNullOrBlank() },
        yearOfBirth = null,
        isWizard = true,
        ancestry = attr.bloodStatus.takeIf { !it.isNullOrBlank() },
        eyeColour = attr.eyeColor.takeIf { !it.isNullOrBlank() },
        hairColour = attr.hairColor.takeIf { !it.isNullOrBlank() },
        wands = attr.wands,
        patronus = attr.patronus.takeIf { !it.isNullOrBlank() },
        isHogwartsStudent = false,
        isHogwartsStaff = false,
        actor = null,
        alternateActors = emptyList(),
        isAlive = true,
        imageUrl = attr.image.takeIf { !it.isNullOrBlank() },
    )
}

fun PotterDbMetaDto?.toDomain(): PaginationMeta? {
    val pag = this?.pagination ?: return null
    val current = pag.current ?: 1
    val next = pag.next
    val last = pag.last ?: 1
    val records = pag.records ?: 0

    return PaginationMeta(
        currentPage = current,
        nextPage = next,
        lastPage = last,
        totalRecords = records,
        hasNextPage = next != null,
    )
}
