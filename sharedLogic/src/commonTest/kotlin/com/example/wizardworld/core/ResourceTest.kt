package com.example.wizardworld.core

import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTest {
    @Test
    fun offline_keepsCachedDataAndMessage() {
        val cached = listOf("Harry", "Hermione")
        val resource = Resource.Offline(data = cached, message = "No connection")

        assertEquals(cached, resource.data)
        assertEquals("No connection", resource.message)
    }

    @Test
    fun error_hasOnlyMessage() {
        val resource = Resource.Error(message = "Network error")

        assertEquals("Network error", resource.message)
    }

    @Test
    fun loadingAndError_areAssignableToAnyResourceType() {
        val states: List<Resource<List<String>>> =
            listOf(
                Resource.Loading,
                Resource.Success(listOf("Harry")),
                Resource.Offline(listOf("Ron"), message = "Server error"),
                Resource.Error(message = "Rate limit"),
            )

        val names =
            states.map { resource ->
                when (resource) {
                    Resource.Loading -> "loading"
                    is Resource.Success -> "success"
                    is Resource.Offline -> "offline"
                    is Resource.Error -> "error"
                }
            }

        assertEquals(listOf("loading", "success", "offline", "error"), names)
    }
}
