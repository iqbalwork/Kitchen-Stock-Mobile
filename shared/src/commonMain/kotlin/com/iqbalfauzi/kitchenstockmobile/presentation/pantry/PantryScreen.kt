package com.iqbalfauzi.kitchenstockmobile.presentation.pantry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PantryScreen(
    onNavigateToDetail: (String?) -> Unit,
    viewModel: PantryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PantryContent(
        uiState = uiState,
        onIntent = viewModel::onIntent,
        onAddClick = { onNavigateToDetail(null) }
    )
}
