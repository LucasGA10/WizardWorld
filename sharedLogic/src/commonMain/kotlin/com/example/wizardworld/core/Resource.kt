package com.example.wizardworld.core

/**
 * Resultado de una operación que puede tardar o fallar, emitido por los repositorios.
 *
 * - [Loading]: la operación está en curso.
 * - [Success]: datos frescos obtenidos de la API.
 * - [Offline]: la API falló (sin conexión, error del servidor o rate limit) y se
 *   devuelven los últimos datos guardados en caché. La UI debe mostrarlos e
 *   informar que está viendo datos sin conexión.
 * - [Error]: la API falló y no hay datos en caché.
 */
sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>

    data class Success<T>(
        val data: T,
    ) : Resource<T>

    data class Offline<T>(
        val data: T,
        val message: String,
    ) : Resource<T>

    data class Error(
        val message: String,
    ) : Resource<Nothing>
}
