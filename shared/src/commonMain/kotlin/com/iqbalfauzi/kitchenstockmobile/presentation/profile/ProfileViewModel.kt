package com.iqbalfauzi.kitchenstockmobile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()

    fun onLogoutClick() {
        _state.update { it.copy(showLogoutConfirmation = true) }
    }

    fun onDismissLogoutConfirmation() {
        _state.update { it.copy(showLogoutConfirmation = false) }
    }

    fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, showLogoutConfirmation = false) }
            try {
                authRepository.signOut()
            } catch (e: Exception) {
                // Ignore sign out errors to ensure local logout still completes
            } finally {
                _state.update { it.copy(isLoading = false, isLoggedOut = true) }
            }
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isLoggedOut: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val error: String? = null
)
