package com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.model.InventoryDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryDetailViewModel(
    private val itemId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryDetailUiState(id = itemId))
    val uiState: StateFlow<InventoryDetailUiState> = _uiState.asStateFlow()

    init {
        if (itemId != null) {
            loadItem(itemId)
        }
    }

    fun onIntent(intent: InventoryDetailIntent) {
        when (intent) {
            is InventoryDetailIntent.UpdateName -> _uiState.value = _uiState.value.copy(name = intent.name)
            is InventoryDetailIntent.SelectLocation -> _uiState.value = _uiState.value.copy(location = intent.location)
            is InventoryDetailIntent.UpdateQuantity -> _uiState.value = _uiState.value.copy(quantity = intent.quantity)
            is InventoryDetailIntent.UpdateUnit -> _uiState.value = _uiState.value.copy(unit = intent.unit)
            is InventoryDetailIntent.UpdateExpiryDate -> _uiState.value = _uiState.value.copy(expiryDate = intent.date)
            InventoryDetailIntent.Save -> saveItem()
            InventoryDetailIntent.Delete -> deleteItem()
        }
    }

    private fun loadItem(id: String) {
        viewModelScope.launch {
            // Simulated loading for update
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Mock loaded data
            _uiState.value = _uiState.value.copy(
                name = "Organic Milk",
                location = "Fridge",
                quantity = 1,
                unit = "Units",
                expiryDate = "20/12/2026",
                isLoading = false
            )
        }
    }

    private fun saveItem() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            // Simulated network call
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
        }
    }

    private fun deleteItem() {
        // Handle deletion
    }
}

sealed interface InventoryDetailIntent {
    data class UpdateName(val name: String) : InventoryDetailIntent
    data class SelectLocation(val location: String) : InventoryDetailIntent
    data class UpdateQuantity(val quantity: Int) : InventoryDetailIntent
    data class UpdateUnit(val unit: String) : InventoryDetailIntent
    data class UpdateExpiryDate(val date: String) : InventoryDetailIntent
    data object Save : InventoryDetailIntent
    data object Delete : InventoryDetailIntent
}
