package ru.practicum.shoppinglist.ui.authorization.viewmodel

import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.practicum.shoppinglist.R
import ru.practicum.shoppinglist.data.model.NetworkState
import ru.practicum.shoppinglist.domain.repository.AuthInteractor
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthorizationState())
    val uiState: StateFlow<AuthorizationState> = _uiState.asStateFlow()

    fun processIntent(intent: AuthorizationIntent) = when (intent) {
        is AuthorizationIntent.SetCurrentEmail -> updateEmail(intent.email)
        is AuthorizationIntent.SetCurrentPassword -> updatePassword(intent.password)
        is AuthorizationIntent.Authorization -> handleAuthorization()
        else -> {}
    }

    private fun handleAuthorization() {
        val email = _uiState.value.currentEmail
        val password = _uiState.value.currentPassword
        _uiState.update { it.copy(isAuthorizationActive = false, errorMessage = null, user = null) }
        viewModelScope.launch {
            interactor.login(email, password).collect { result ->
                if (result.data != null) {
                    _uiState.update {
                        it.copy(
                            user = result.data,
                            isAuthorizationActive = true,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isAuthorizationActive = false,
                            errorMessage = mapError(result.message)
                        )
                    }
                }
            }
        }
    }

    private fun mapError(message: String?): Int? {
        return when (message) {
            NetworkState.NoConnection.value -> R.string.error_no_connection
            NetworkState.IncorrectEmail.value -> R.string.error_invalid_email
            NetworkState.ShortPassword.value -> R.string.error_password_length
            else -> if (message != null) R.string.error_authorization else null
        }
    }

    private fun updateEmail(email: String) {
        _uiState.update {
            it.copy(currentEmail = email)
        }
        emailValidate(email)
        updateRegistrationButtonState()
    }

    private fun updatePassword(password: String) {
        _uiState.update {
            it.copy(currentPassword = password)
        }
        validatePasswords()
        updateRegistrationButtonState()
    }

    private fun validatePasswords() {
        checkPasswordValidation(
            _uiState.value.currentPassword,
        )
    }

    private fun updateRegistrationButtonState() {
        val email = _uiState.value.currentEmail
        val password = _uiState.value.currentPassword
        if (emailValidate(email) && checkPasswordValidation(password)) {
            _uiState.update { it.copy(isAuthorizationActive = true) }
        } else {
            _uiState.update { it.copy(isAuthorizationActive = false) }
        }
    }

    private fun emailValidate(email: String?): Boolean {
        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = R.string.error_empty_fields) }
            return false
        }
        val trimmedEmail = email.trim()
        val isValid = Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = if (!isValid) R.string.error_invalid_email else null
            )
        }
        return isValid
    }

    private fun checkPasswordValidation(password: String): Boolean {
        val errorMessage = when {
            password.isBlank() -> R.string.error_empty_fields
            password.length < PASSWORD_LENGTH -> R.string.error_password_length
            else -> null
        }
        _uiState.update { it.copy(errorMessage = errorMessage) }
        return errorMessage == null
    }

    companion object {
        const val PASSWORD_LENGTH = 7
    }
}