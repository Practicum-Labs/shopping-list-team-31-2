package ru.practicum.shoppinglist.data.network.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import ru.practicum.shoppinglist.data.network.model.request.LoginRequest
import ru.practicum.shoppinglist.data.network.model.request.RefreshTokenRequest
import ru.practicum.shoppinglist.data.network.model.response.CheckResponse
import ru.practicum.shoppinglist.data.network.model.response.LoginResponse
import ru.practicum.shoppinglist.data.network.model.response.RefreshTokenResponse

interface AuthApiService {

    @POST("auth/registration")
    suspend fun register(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/recovery")
    suspend fun recovery(
        @Header("email") email: String
    ): String

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): RefreshTokenResponse

    @GET("auth/check")
    suspend fun checkAuth(
        @Header("Authorization") token: String
    ): CheckResponse

}