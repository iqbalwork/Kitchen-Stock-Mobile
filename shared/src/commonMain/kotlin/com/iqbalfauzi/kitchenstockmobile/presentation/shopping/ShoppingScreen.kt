package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ShoppingContent(
        uiState = uiState
    )
}
