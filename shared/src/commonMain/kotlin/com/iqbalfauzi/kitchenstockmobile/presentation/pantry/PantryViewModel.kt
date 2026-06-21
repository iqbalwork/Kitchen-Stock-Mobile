package com.iqbalfauzi.kitchenstockmobile.presentation.pantry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.WaterDrop
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.ExpiryStatus
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.PantryItem
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.PantryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PantryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PantryUiState())
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onIntent(intent: PantryIntent) {
        when (intent) {
            is PantryIntent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategory = intent.category)
            }
            is PantryIntent.UpdateQuantity -> {
                // Handle quantity update
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val items = listOf(
                PantryItem(
                    "1", "Greek Yogurt", 1, "500g", "Fridge",
                    Icons.Default.WaterDrop, ExpiryStatus.Warning("Expires in 3 days")
                ),
                PantryItem(
                    "2", "Chicken Breast", 2, "units", "Fridge",
                    Icons.Default.WaterDrop, ExpiryStatus.Critical("Expires Tomorrow")
                ),
                PantryItem(
                    "3", "Avocados", 4, "units", "Pantry",
                    Icons.Default.Eco, ExpiryStatus.Normal()
                ),
                PantryItem(
                    "4", "Jasmine Rice", 1, "1.5kg", "Pantry",
                    Icons.Default.Grain, ExpiryStatus.Normal()
                )
            )

            _uiState.value = _uiState.value.copy(
                groupedItems = items.groupBy { it.location.uppercase() }
            )
        }
    }
}

sealed interface PantryIntent {
    data class SelectCategory(val category: String) : PantryIntent
    data class UpdateQuantity(val id: String, val newQuantity: Int) : PantryIntent
}
