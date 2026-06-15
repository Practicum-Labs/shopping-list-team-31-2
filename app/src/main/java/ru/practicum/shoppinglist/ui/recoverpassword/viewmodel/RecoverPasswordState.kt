package ru.practicum.shoppinglist.ui.recoverpassword.viewmodel

data class RecoverPasswordState(
    val errorMessage: Int? = null,
    val currentEmail: String = "",
    val isRecoverPasswordActive: Boolean = false,
    val isReadyBackToAuth: Boolean = false
)