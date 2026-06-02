package ru.practicum.shoppinglist.data.network.api

import retrofit2.Response
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
    ): Response<LoginResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("auth/recovery")
    suspend fun recovery(
        @Header("email") email: String
    ): Response<String>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @GET("auth/check")
    suspend fun checkAuth(
        @Header("Authorization") token: String
    ): Response<CheckResponse>

}