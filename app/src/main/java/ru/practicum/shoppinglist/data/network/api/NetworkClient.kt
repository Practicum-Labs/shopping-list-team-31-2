package ru.practicum.shoppinglist.data.network.api

import ru.practicum.shoppinglist.data.network.model.NetworkResponse
import ru.practicum.shoppinglist.data.network.model.request.LoginRequest

interface NetworkClient {
    suspend fun doRequestLogin(
        user: LoginRequest
    ): NetworkResponse

    suspend fun doRequestRegistration(
        user: LoginRequest
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