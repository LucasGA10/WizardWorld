package com.example.wizardworld.data.remote.mapper

import com.example.wizardworld.data.remote.dto.HpCharacterDto
import com.example.wizardworld.data.remote.dto.WandDto
import com.example.wizardworld.domain.model.HpCharacter
import com.example.wizardworld.domain.model.Wand

fun HpCharacterDto.toDomain(): HpCharacter =
    HpCharacter(
        id = id,
        name = name,
        alternateNames = alternateNames,
        species = species.takeIf { !it.isNullOrBlank() },
        gender = gender.takeIf { !it.isNullOrBlank() },
        house = house.takeIf { !it.isNullOrBlank() },
        dateOfBirth = dateOfBirth.takeIf { !it.isNullOrBlank() },
        yearOfBirth = yearOfBirth,
        isWizard = wizard,
        ancestry = ancestry.takeIf { !it.isNullOrBlank() },
        eyeColour = eyeColour.takeIf { !it.isNullOrBlank() },
        hairColour = hairColour.takeIf { !it.isNullOrBlank() },
        wand = wand?.toDomain(),
        patronus = patronus.takeIf { !it.isNullOrBlank() },
        isHogwartsStudent = hogwartsStudent,
        isHogwartsStaff = hogwartsStaff,
        actor = actor.takeIf { !it.isNullOrBlank() },
        alternateActors = alternateActors,
        isAlive = alive,
        imageUrl = image.takeIf { !it.isNullOrBlank() },
    )

fun WandDto.toDomain(): Wand =
    Wand(
        wood = wood.takeIf { !it.isNullOrBlank() },
        core = core.takeIf { !it.isNullOrBlank() },
        length = length,
    )
