package com.iqbalfauzi.kitchenstock.presentation.auth

data class ForgotPasswordScreenState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)
