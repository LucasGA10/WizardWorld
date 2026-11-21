package com.example.wizardworld.domain.model

data class PaginationMeta(
    val currentPage: Int = 1,
    val nextPage: Int? = null,
    val lastPage: Int = 1,
    val totalRecords: Int = 0,
    val hasNextPage: Boolean = nextPage != null,
)
