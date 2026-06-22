package ru.practicum.shoppinglist.ui.registration.viewmodel

import ru.practicum.shoppinglist.domain.model.User

data class RegistrationState(
    val user: User? = null,
    val errorMessage: Int? = null,
    val currentEmail: String = "",
    val currentPassword: String = "",
    val currentRepeatPassword: String = "",
    val isRegistrationActive: Boolean = false
)