package com.iqbalfauzi.kitchenstockmobile.presentation.pantry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetInventoryItemsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.ExpiryStatus
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.PantryItem
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.model.PantryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class PantryViewModel(
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val getStorageLocationsUseCase: GetStorageLocationsUseCase,
    private val upsertInventoryItemUseCase: UpsertInventoryItemUseCase,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PantryUiState())
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    private var allItems: List<InventoryItem> = emptyList()

    init {
        observeData()
        syncData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getInventoryItemsUseCase(),
                getStorageLocationsUseCase(),
                _uiState
            ) { items, locations, state ->
                allItems = items
                
                val filteredItems = if (state.selectedCategoryId == null) {
                    items
                } else {
                    items.filter { it.storageLocationId == state.selectedCategoryId }
                }
                
                Triple(filteredItems.map { it.toPantryItem() }, locations, state.selectedCategoryId)
            }.collect { (pantryItems, locations, selectedId) ->
                _uiState.value = _uiState.value.copy(
                    categories = locations,
                    selectedCategoryId = selectedId,
                    groupedItems = pantryItems.groupBy { it.location.uppercase() }
                )
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            try {
                inventoryRepository.syncInventory()
            } catch (_: Exception) {
            }
        }
    }

    fun onIntent(intent: PantryIntent) {
        when (intent) {
            is PantryIntent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(selectedCategoryId = intent.categoryId)
            }
            is PantryIntent.UpdateQuantity -> {
                updateQuantity(intent.id, intent.newQuantity)
            }
            PantryIntent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            try {
                inventoryRepository.syncInventory()
            } catch (_: Exception) {
            } finally {
                _uiState.value = _uiState.value.copy(isRefreshing = false)
            }
        }
    }

    private fun updateQuantity(id: String, newQuantity: Int) {
        if (newQuantity < 0) return
        viewModelScope.launch {
            val item = allItems.find { it.id == id } ?: return@launch
            val updatedItem = item.copy(
                quantity = newQuantity.toDouble(),
                updatedAt = Clock.System.now()
            )
            upsertInventoryItemUseCase(updatedItem)
        }
    }

    private fun InventoryItem.toPantryItem(): PantryItem {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val status = when {
            expiryDate == null -> ExpiryStatus.Normal()
            expiryDate < today -> ExpiryStatus.Critical("Expired")
            today.daysUntil(expiryDate) <= 3 -> ExpiryStatus.Warning("Expires in ${today.daysUntil(expiryDate)} days")
            else -> ExpiryStatus.Normal()
        }

        val locationName = location?.name ?: "Unknown"
        val icon = when {
            locationName.contains("Fridge", ignoreCase = true) -> Icons.Default.Kitchen
            locationName.contains("Pantry", ignoreCase = true) -> Icons.Default.Inventory2
            locationName.contains("Freezer", ignoreCase = true) -> Icons.Default.AcUnit
            else -> Icons.Default.Coffee
        }

        return PantryItem(
            id = id,
            name = product?.name ?: "Unknown",
            quantity = quantity.toInt(),
            unit = product?.unit ?: "",
            location = locationName,
            icon = icon,
            expiryStatus = status
        )
    }
}

sealed interface PantryIntent {
    data class SelectCategory(val categoryId: String?) : PantryIntent
    data class UpdateQuantity(val id: String, val newQuantity: Int) : PantryIntent
    data object Refresh : PantryIntent
}
