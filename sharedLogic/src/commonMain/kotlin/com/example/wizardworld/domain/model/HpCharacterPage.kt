package com.example.wizardworld.domain.model

data class HpCharacterPage(
    val characters: List<HpCharacter>,
    val paginationMeta: PaginationMeta? = null,
)
