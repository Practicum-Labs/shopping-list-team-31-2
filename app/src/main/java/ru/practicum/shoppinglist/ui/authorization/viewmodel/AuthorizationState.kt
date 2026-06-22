package ru.practicum.shoppinglist.ui.authorization.viewmodel

import ru.practicum.shoppinglist.domain.model.User

data class AuthorizationState(
    val user: User? = null,
    val errorMessage: Int? = null,
    val currentEmail: String = "",
    val currentPassword: String = "",
    val isAuthorizationActive: Boolean = false
)