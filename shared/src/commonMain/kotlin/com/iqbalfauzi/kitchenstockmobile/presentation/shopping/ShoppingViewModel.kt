package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetShoppingListUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.ToggleShoppingItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.DeleteShoppingItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.model.ShoppingItem
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.model.ShoppingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ShoppingViewModel(
    private val getShoppingListUseCase: GetShoppingListUseCase,
    private val toggleShoppingItemUseCase: ToggleShoppingItemUseCase,
    private val deleteShoppingItemUseCase: DeleteShoppingItemUseCase,
    private val upsertInventoryItemUseCase: UpsertInventoryItemUseCase,
    private val getStorageLocationsUseCase: GetStorageLocationsUseCase,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _storageLocations = MutableStateFlow<List<StorageLocation>>(emptyList())
    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<ShoppingUiState> = combine(
        getShoppingListUseCase(),
        _searchQuery,
        _isRefreshing
    ) { items, query, refreshing ->
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
            recentlyBought = listOf("Eggs", "Bread", "Coffee Beans"),
            isRefreshing = refreshing
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShoppingUiState()
    )

    init {
        syncData()
        loadStorageLocations()
    }

    private fun loadStorageLocations() {
        viewModelScope.launch {
            getStorageLocationsUseCase().collect {
                _storageLocations.value = it
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                shoppingRepository.syncShoppingList()
            } catch (e: Exception) {
                // Log error
            } finally {
                _isRefreshing.value = false
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
                addItemToInventory(intent.id)
            }
            ShoppingIntent.Refresh -> syncData()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun addItemToInventory(id: String) {
        viewModelScope.launch {
            // 1. Find the shopping item
            val shoppingItem = getShoppingListUseCase().first().find { it.id == id } ?: return@launch
            
            // 2. Find "Lemari Pantry" location
            val pantryLocation = _storageLocations.value.find { 
                it.name.contains("Lemari Pantry", ignoreCase = true) 
            } ?: _storageLocations.value.firstOrNull() // Fallback to first if not found

            if (pantryLocation != null) {
                val inventoryItem = com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem(
                    id = Uuid.random().toString(),
                    productId = shoppingItem.productId,
                    storageLocationId = pantryLocation.id,
                    quantity = shoppingItem.quantity,
                    expiryDate = null,
                    updatedAt = kotlin.time.Clock.System.now(),
                    product = null,
                    location = null
                )
                
                // 3. Add to inventory
                upsertInventoryItemUseCase(inventoryItem)
                
                // 4. Remove from shopping list
                deleteShoppingItemUseCase(id)
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
    data object Refresh : ShoppingIntent
}
