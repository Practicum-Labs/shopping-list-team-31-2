package ru.practicum.shoppinglist.ui.authorization.viewmodel

interface AuthorizationIntent {
    data class SetCurrentEmail(val email: String) : AuthorizationIntent
    data class SetCurrentPassword(val password: String) : AuthorizationIntent
    data object Authorization : AuthorizationIntent
}