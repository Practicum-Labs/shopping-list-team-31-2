package ru.practicum.shoppinglist.ui.registration.viewmodel

interface RegistrationIntent {
    data class SetCurrentEmail(val email: String) : RegistrationIntent
    data class SetCurrentPassword(val password: String) : RegistrationIntent
    data class SetCurrentRepeatPassword(val repeatPassword: String) : RegistrationIntent
    data object Registration : RegistrationIntent
}