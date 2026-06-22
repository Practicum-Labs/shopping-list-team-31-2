package ru.practicum.shoppinglist.ui.recoverpassword.viewmodel

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
class RecoverPasswordViewModel @Inject constructor(
    private val interactor: AuthInteractor,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecoverPasswordState())
    val uiState: StateFlow<RecoverPasswordState> = _uiState.asStateFlow()

    fun processIntent(intent: RecoverPasswordIntent) = when (intent) {
        is RecoverPasswordIntent.SetCurrentEmail -> updateEmail(intent.email)
        is RecoverPasswordIntent.RecoverPassword -> handleRecoverPassword()
        else -> {}
    }

    private fun handleRecoverPassword() {
        val email = _uiState.value.currentEmail
        _uiState.update { it.copy(isRecoverPasswordActive = false, errorMessage = null) }
        viewModelScope.launch {
            interactor.recoverPassword(email).collect { result ->
                if (result.data != null) {
                    _uiState.update {
                        it.copy(
                            isRecoverPasswordActive = true,
                            errorMessage = null,
                            isReadyBackToAuth = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isRecoverPasswordActive = false,
                            isReadyBackToAuth = false,
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
            else -> if (message != null) R.string.error_invalid_email else null
        }
    }

    private fun updateEmail(email: String) {
        _uiState.update {
            it.copy(currentEmail = email)
        }
        emailValidate(email)
        updateRecoverPasswordButtonState()
    }

    private fun updateRecoverPasswordButtonState() {
        val email = _uiState.value.currentEmail
        if (emailValidate(email)) {
            _uiState.update { it.copy(isRecoverPasswordActive = true) }
        } else {
            _uiState.update { it.copy(isRecoverPasswordActive = false) }
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

}