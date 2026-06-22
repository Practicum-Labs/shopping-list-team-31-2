package ru.practicum.shoppinglist.data.network.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)