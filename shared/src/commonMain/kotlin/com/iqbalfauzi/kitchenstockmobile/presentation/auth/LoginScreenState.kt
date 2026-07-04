package com.iqbalfauzi.kitchenstockmobile.presentation.auth

data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
