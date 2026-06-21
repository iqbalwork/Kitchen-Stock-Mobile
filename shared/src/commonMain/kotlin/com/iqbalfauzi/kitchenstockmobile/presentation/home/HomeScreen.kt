package com.iqbalfauzi.kitchenstockmobile.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (String?) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState = uiState,
        onAddClick = { onNavigateToDetail(null) },
        onNotificationClick = { /* Handle notifications */ },
        onInventoryClick = { /* Handle inventory */ }
    )
}
