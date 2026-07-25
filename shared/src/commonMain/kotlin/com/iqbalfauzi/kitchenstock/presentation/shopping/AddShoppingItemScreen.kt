package com.iqbalfauzi.kitchenstock.presentation.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun AddShoppingItemScreen(
    onBackClick: () -> Unit,
    viewModel: AddShoppingItemViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(AddShoppingItemIntent.ResetForm)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
            viewModel.onIntent(AddShoppingItemIntent.ResetForm)
        }
    }

    AddShoppingItemContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick
    )
}
