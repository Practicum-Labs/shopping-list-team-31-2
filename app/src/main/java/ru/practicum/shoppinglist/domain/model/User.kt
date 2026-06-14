package ru.practicum.shoppinglist.domain.model

data class User(
    val userId: Long,
    var accessToken: String,
    var refreshToken: String,
    var lastUpdateToken: Long
)
