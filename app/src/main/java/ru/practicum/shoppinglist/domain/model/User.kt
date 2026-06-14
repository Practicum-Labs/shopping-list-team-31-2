package ru.practicum.shoppinglist.domain.model

data class User(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String
)
