package com.iqbalfauzi.kitchenstock.presentation.inventory_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun InventoryDetailScreen(
    id: String? = null,
    onBackClick: () -> Unit,
    viewModel: InventoryDetailViewModel = koinViewModel { parametersOf(id) }
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(id) {
        if (id == null) {
            viewModel.onIntent(InventoryDetailIntent.ResetForm)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
            // Reset form for next time it's opened
            viewModel.onIntent(InventoryDetailIntent.ResetForm)
        }
    }

    InventoryDetailContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick
    )
}
