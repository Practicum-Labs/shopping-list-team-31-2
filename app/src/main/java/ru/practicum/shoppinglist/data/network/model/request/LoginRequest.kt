package ru.practicum.shoppinglist.data.network.model.request

data class LoginRequest(
    val email: String,
    val password: String
)