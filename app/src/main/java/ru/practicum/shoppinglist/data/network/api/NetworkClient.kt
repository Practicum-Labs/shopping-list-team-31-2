package ru.practicum.shoppinglist.data.network.api

interface NetworkClient {
    suspend fun doRequestLogin(
        email: String, password: String
    ): NetworkClient

    suspend fun doRequestRegistration(
        email: String, password: String
    ): NetworkClient

    suspend fun doRequestRecovery(
        email: String
    ): NetworkClient

    suspend fun doRequestRefresh(
        refreshToken: String
    ): NetworkClient

    suspend fun doRequestCheck(
        accessToken: String
    ): NetworkClient
}