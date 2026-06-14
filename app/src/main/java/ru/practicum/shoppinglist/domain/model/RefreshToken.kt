package ru.practicum.shoppinglist.domain.model

data class RefreshToken(
    val accessToken: String,
    val refreshToken: String
)
