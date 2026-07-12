package com.iqbalfauzi.kitchenstock.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstock.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginScreenState())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onPasswordVisibilityToggle() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val currentState = _state.value
        if (currentState.email.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(error = "Please fill in all fields") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithEmail(currentState.email, currentState.password)
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Login failed") }
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithGoogle()
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Google Sign-In failed") }
            }
        }
    }

    fun signInWithApple() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.signInWithApple()
                _state.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Apple Sign-In failed") }
            }
        }
    }
}
