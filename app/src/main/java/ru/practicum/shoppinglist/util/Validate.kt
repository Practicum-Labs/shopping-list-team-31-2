package ru.practicum.shoppinglist.util

import android.util.Patterns
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ru.practicum.shoppinglist.ui.registration.viewmodel.RegistrationState

class Validate (uiState: StateFlow<RegistrationState>) {

//    fun emailValidate(email: String?): Boolean {
//        if (email.isNullOrBlank()) {
//            uiState.update { it.copy(errorMessage = null) }
//            return false
//        }
//        val trimmedEmail = email.trim()
//        val isValid = Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
//        uiState.update { currentState ->
//            currentState.copy(
//                errorMessage = if (!isValid) "Некорректный email" else null
//            )
//        }
//        return isValid
//    }
//
//    fun passwordValidate(password: String?, repeatPassword: String?): Boolean {
//        if (password.isNullOrBlank() or repeatPassword.isNullOrBlank()) {
//            uiState.update { it.copy(errorMessage = null) }
//            return false
//        }
//        if ((password?.length ?: 0) <= 6) {
//            uiState.update {
//                it.copy(errorMessage = "Пароль меньше 6 символов")
//            }
//            return false
//        } else if (password == repeatPassword) {
//            uiState.update {
//                it.copy(errorMessage = "Пароли не совпадают")
//            }
//            return false
//        } else {
//            return true
//        }
//    }
}