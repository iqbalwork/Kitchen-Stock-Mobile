package com.iqbalfauzi.kitchenstockmobile.presentation.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.WaterDrop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.AttentionItem
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.AttentionStatus
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData -> loadData()
            HomeIntent.Refresh -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // Simulated loading
            _uiState.value = HomeUiState(
                totalItems = 42,
                expiringCount = 5,
                outOfStockCount = 3,
                attentionItems = listOf(
                    AttentionItem(
                        "1", "Whole Milk", "200ml remaining",
                        AttentionStatus.Expiring("Exp. in 2 Days"), Icons.Default.WaterDrop
                    ),
                    AttentionItem(
                        "2", "Large Eggs", "0 remaining",
                        AttentionStatus.OutOfStock(), Icons.Default.Egg
                    ),
                    AttentionItem(
                        "3", "Spinach", "1 bag",
                        AttentionStatus.Expiring("Exp. in 1 Day"), Icons.Default.Eco
                    ),
                    AttentionItem(
                        "4", "Bread", "2 slices",
                        AttentionStatus.LowStock(), Icons.Default.BakeryDining
                    )
                )
            )
        }
    }
}

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data object Refresh : HomeIntent
}
