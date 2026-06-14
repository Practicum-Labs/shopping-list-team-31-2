package ru.practicum.shoppinglist.data.network.api

import ru.practicum.shoppinglist.data.network.model.NetworkResponse

interface NetworkClient {
    suspend fun doRequestLogin(
        email: String, password: String
    ): NetworkResponse

    suspend fun doRequestRegistration(
        email: String, password: String
    ): NetworkResponse

    suspend fun doRequestRecovery(
        email: String
    ): NetworkResponse

    suspend fun doRequestRefresh(
        refreshToken: String
    ): NetworkResponse

    suspend fun doRequestCheck(
        accessToken: String
    ): NetworkResponse
}