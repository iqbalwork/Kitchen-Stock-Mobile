package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetShoppingListUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.ToggleShoppingItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.DeleteShoppingItemUseCase
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.model.ShoppingItem
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.model.ShoppingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingViewModel(
    private val getShoppingListUseCase: GetShoppingListUseCase,
    private val toggleShoppingItemUseCase: ToggleShoppingItemUseCase,
    private val deleteShoppingItemUseCase: DeleteShoppingItemUseCase,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ShoppingUiState> = combine(
        getShoppingListUseCase(),
        _searchQuery
    ) { items, query ->
        val filteredItems = if (query.isBlank()) {
            items
        } else {
            items.filter { it.product?.name?.contains(query, ignoreCase = true) == true }
        }

        val grouped = filteredItems.map { item ->
            ShoppingItem(
                id = item.id,
                name = item.product?.name ?: "Unknown Product",
                quantity = "${item.quantity} ${item.product?.unit ?: ""}",
                isChecked = item.isBought
            ) to (item.product?.category?.name ?: "Uncategorized")
        }.groupBy({ it.second }, { it.first })

        ShoppingUiState(
            searchQuery = query,
            groupedItems = grouped,
            recentlyBought = listOf("Eggs", "Bread", "Coffee Beans")
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShoppingUiState()
    )

    init {
        syncData()
    }

    private fun syncData() {
        viewModelScope.launch {
            try {
                shoppingRepository.syncShoppingList()
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun onIntent(intent: ShoppingIntent) {
        when (intent) {
            is ShoppingIntent.ToggleItem -> toggleItem(intent.id)
            is ShoppingIntent.UpdateSearch -> {
                _searchQuery.value = intent.query
            }
            is ShoppingIntent.AddItemToInventory -> {
                viewModelScope.launch {
                    deleteShoppingItemUseCase(intent.id)
                }
            }
        }
    }

    private fun toggleItem(id: String) {
        viewModelScope.launch {
            val item = uiState.value.groupedItems.values.flatten().firstOrNull { it.id == id }
            if (item != null) {
                toggleShoppingItemUseCase(id, !item.isChecked)
            }
        }
    }
}

sealed interface ShoppingIntent {
    data class ToggleItem(val id: String) : ShoppingIntent
    data class UpdateSearch(val query: String) : ShoppingIntent
    data class AddItemToInventory(val id: String) : ShoppingIntent
}

