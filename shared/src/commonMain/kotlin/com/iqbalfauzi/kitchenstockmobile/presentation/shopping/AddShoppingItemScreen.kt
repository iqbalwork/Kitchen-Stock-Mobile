package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

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

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
            viewModel.resetState()
        }
    }

    AddShoppingItemContent(
        uiState = uiState,
        onProductSelected = viewModel::selectProduct,
        onQuantityChanged = viewModel::updateQuantity,
        onAddClick = viewModel::addShoppingItem,
        onBackClick = onBackClick
    )
}
