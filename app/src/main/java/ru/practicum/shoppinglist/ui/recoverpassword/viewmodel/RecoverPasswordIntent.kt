package ru.practicum.shoppinglist.ui.recoverpassword.viewmodel

interface RecoverPasswordIntent {
    data class SetCurrentEmail(val email: String) : RecoverPasswordIntent
    data object RecoverPassword : RecoverPasswordIntent
}