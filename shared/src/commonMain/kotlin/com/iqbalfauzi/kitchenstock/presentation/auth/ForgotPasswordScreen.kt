package com.iqbalfauzi.kitchenstock.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    ForgotPasswordContent(
        state = state,
        onEmailChanged = viewModel::onEmailChanged,
        onSendResetLinkClick = viewModel::sendResetLink,
        onBackClick = onBackClick
    )
}
