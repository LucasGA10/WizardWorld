package com.example.wizardworld.presentation

import com.example.wizardworld.core.Resource
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceToUiStateTest {
    private val isEmpty: (List<String>) -> Boolean = { it.isEmpty() }

    @Test
    fun loading_mapsToLoading() {
        val resource: Resource<List<String>> = Resource.Loading

        assertEquals(UiState.Loading, resource.toUiState(isEmpty))
    }

    @Test
    fun success_mapsToOnlineSuccess() {
        val resource = Resource.Success(listOf("Harry"))

        assertEquals(
            UiState.Success(data = listOf("Harry"), isOffline = false),
            resource.toUiState(isEmpty),
        )
    }

    @Test
    fun successWithEmptyList_mapsToEmpty() {
        val resource = Resource.Success(emptyList<String>())

        assertEquals(UiState.Empty, resource.toUiState(isEmpty))
    }

    @Test
    fun offline_mapsToOfflineSuccess() {
        val resource = Resource.Offline(data = listOf("Ron"), message = "No connection")

        assertEquals(
            UiState.Success(data = listOf("Ron"), isOffline = true),
            resource.toUiState(isEmpty),
        )
    }

    @Test
    fun error_mapsToErrorWithMessage() {
        val resource: Resource<List<String>> = Resource.Error(message = "Server error")

        assertEquals(UiState.Error(message = "Server error"), resource.toUiState(isEmpty))
    }
}
