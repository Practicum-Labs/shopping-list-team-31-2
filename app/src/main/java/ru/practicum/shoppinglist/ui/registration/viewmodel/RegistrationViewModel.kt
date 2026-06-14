package ru.practicum.shoppinglist.ui.registration.viewmodel

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.domain.repository.AuthInteractor
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationState())
    val uiState: StateFlow<RegistrationState> = _uiState.asStateFlow()

    fun processIntent(intent: RegistrationIntent) = when (intent) {
        is RegistrationIntent.SetCurrentEmail -> {
            _uiState.update { it.copy(currentEmail = intent.email) }
            emailValidate(intent.email)
            updateRegistrationButtonState()
        }

        is RegistrationIntent.SetCurrentPassword -> {
            _uiState.update { it.copy(currentPassword = intent.password) }
            checkPasswordValidation(intent.password, _uiState.value.currentRepeatPassword)
            updateRegistrationButtonState()
        }

        is RegistrationIntent.SetCurrentRepeatPassword -> {
            _uiState.update { it.copy(currentRepeatPassword = intent.repeatPassword) }
            checkPasswordValidation(_uiState.value.currentPassword, intent.repeatPassword)
            updateRegistrationButtonState()
        }

        is RegistrationIntent.Registration -> handleRegistration()
        else -> {}
    }

    private fun handleRegistration() {
        val email = _uiState.value.currentEmail
        val password = _uiState.value.currentPassword
        _uiState.update { it.copy(isRegistrationActive = true) }
        viewModelScope.launch {
            interactor.registration(email, password).catch { e ->
                _uiState.update { it.copy(errorMessage = e.message) }
            }.collect { result ->
                _uiState.update {
                    it.copy(user = result.data)
                }
            }
        }
    }


    private fun updateRegistrationButtonState() {
        val email = _uiState.value.currentEmail
        val password = _uiState.value.currentPassword
        val repeatPassword = _uiState.value.currentRepeatPassword
        if (emailValidate(email) && checkPasswordValidation(password, repeatPassword)) {
            _uiState.update { it.copy(isRegistrationActive = true) }
        } else {
            _uiState.update { it.copy(isRegistrationActive = false) }
        }
    }

    private fun emailValidate(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Поле обязательно для заполнения") }
            return false
        }
        val trimmedEmail = email.trim()
        val isValid = Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = if (!isValid) "Некорректный email" else null
            )
        }
        return isValid
    }

    private fun checkPasswordValidation(password: String, repeatPassword: String?): Boolean {
        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Поле обязательно для заполнения") }
            return false
        }
        if (password.length < 7) {
            _uiState.update { it.copy(errorMessage = "Пароль должен быть больше 7 символов") }
            return false
        }
        if (!repeatPassword.isNullOrBlank() && password != repeatPassword) {
            _uiState.update { it.copy(errorMessage = "Пароли не совпадают") }
            return false
        }
        _uiState.update { it.copy(errorMessage = null) }
        return true
    }
}