package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.model.ShoppingItem
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.model.ShoppingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShoppingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onIntent(intent: ShoppingIntent) {
        when (intent) {
            is ShoppingIntent.ToggleItem -> toggleItem(intent.id)
            is ShoppingIntent.UpdateSearch -> {
                _uiState.value = _uiState.value.copy(searchQuery = intent.query)
            }
            is ShoppingIntent.AddItemToInventory -> {
                // Logic to add to inventory and remove from shopping list
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val items = mapOf(
                "Produce" to listOf(
                    ShoppingItem("1", "Bananas", "1 bunch"),
                    ShoppingItem("2", "Spinach", "1 bag")
                ),
                "Dairy & Eggs" to listOf(
                    ShoppingItem("3", "Greek Yogurt", "2 tubs"),
                    ShoppingItem("4", "Milk", "1 gallon", isChecked = true)
                ),
                "Pantry Staples" to listOf(
                    ShoppingItem("5", "Olive Oil", "1L bottle")
                )
            )

            _uiState.value = _uiState.value.copy(
                groupedItems = items,
                recentlyBought = listOf("Eggs", "Bread", "Coffee Beans")
            )
        }
    }

    private fun toggleItem(id: String) {
        val currentGrouped = _uiState.value.groupedItems.toMutableMap()
        for ((category, items) in currentGrouped) {
            val index = items.indexOfFirst { it.id == id }
            if (index != -1) {
                val newItems = items.toMutableList()
                newItems[index] = items[index].copy(isChecked = !items[index].isChecked)
                currentGrouped[category] = newItems
                break
            }
        }
        _uiState.value = _uiState.value.copy(groupedItems = currentGrouped)
    }
}

sealed interface ShoppingIntent {
    data class ToggleItem(val id: String) : ShoppingIntent
    data class UpdateSearch(val query: String) : ShoppingIntent
    data class AddItemToInventory(val id: String) : ShoppingIntent
}
