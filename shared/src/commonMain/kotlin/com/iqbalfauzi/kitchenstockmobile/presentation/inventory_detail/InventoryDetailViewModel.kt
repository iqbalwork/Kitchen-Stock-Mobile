package com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.DeleteInventoryItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetCategoriesUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetInventoryItemByIdUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertProductUseCase
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.model.InventoryDetailUiState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InventoryDetailViewModel(
    private val getInventoryItemByIdUseCase: GetInventoryItemByIdUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getStorageLocationsUseCase: GetStorageLocationsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val upsertInventoryItemUseCase: UpsertInventoryItemUseCase,
    private val upsertProductUseCase: UpsertProductUseCase,
    private val deleteInventoryItemUseCase: DeleteInventoryItemUseCase,
    itemId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryDetailUiState(id = itemId))
    val uiState: StateFlow<InventoryDetailUiState> = _uiState.asStateFlow()

    init {
        loadProductsAndLocationsAndCategories()
        if (itemId != null) {
            loadItem(itemId)
        }
    }

    private fun loadProductsAndLocationsAndCategories() {
        viewModelScope.launch {
            getProductsUseCase().collect { products ->
                _uiState.value = _uiState.value.copy(products = products)
            }
        }
        viewModelScope.launch {
            getStorageLocationsUseCase().collect { locations ->
                _uiState.value = _uiState.value.copy(locations = locations)
            }
        }
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun onIntent(intent: InventoryDetailIntent) {
        when (intent) {
            is InventoryDetailIntent.UpdateName -> {
                val matches = _uiState.value.products.filter { it.name.equals(intent.name, ignoreCase = true) }
                val exactMatch = matches.find { it.name.equals(intent.name, ignoreCase = true) }
                _uiState.value = _uiState.value.copy(
                    name = intent.name,
                    productId = exactMatch?.id ?: "",
                    categoryId = exactMatch?.categoryId ?: _uiState.value.categoryId
                )
            }
            is InventoryDetailIntent.SelectProduct -> {
                val product = _uiState.value.products.find { it.id == intent.productId }
                _uiState.value = _uiState.value.copy(
                    productId = intent.productId,
                    name = product?.name ?: "",
                    unit = product?.unit ?: "Units",
                    categoryId = product?.categoryId ?: _uiState.value.categoryId
                )
            }
            is InventoryDetailIntent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(categoryId = intent.categoryId)
            }
            is InventoryDetailIntent.SelectLocation -> {
                val location = _uiState.value.locations.find { it.id == intent.locationId }
                _uiState.value = _uiState.value.copy(
                    storageLocationId = intent.locationId,
                    location = location?.name ?: ""
                )
            }
            is InventoryDetailIntent.UpdateQuantity -> _uiState.value = _uiState.value.copy(quantity = intent.quantity)
            is InventoryDetailIntent.UpdateUnit -> _uiState.value = _uiState.value.copy(unit = intent.unit)
            is InventoryDetailIntent.UpdateExpiryDate -> _uiState.value = _uiState.value.copy(expiryDate = intent.date)
            InventoryDetailIntent.Save -> saveItem()
            InventoryDetailIntent.Delete -> deleteItem()
            InventoryDetailIntent.ResetSuccess -> _uiState.value = _uiState.value.copy(isSuccess = false)
            InventoryDetailIntent.ResetForm -> resetForm()
        }
    }

    private fun resetForm() {
        _uiState.value = _uiState.value.copy(
            id = null,
            productId = "",
            name = "",
            categoryId = "",
            quantity = 1,
            expiryDate = "",
            isSuccess = false
        )
    }

    private fun loadItem(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getInventoryItemByIdUseCase(id).collect { item ->
                if (item != null) {
                    _uiState.value = _uiState.value.copy(
                        id = item.id,
                        productId = item.productId,
                        name = item.product?.name ?: "",
                        categoryId = item.product?.categoryId ?: "",
                        storageLocationId = item.storageLocationId,
                        location = item.location?.name ?: "",
                        quantity = item.quantity.toInt(),
                        unit = item.product?.unit ?: "Units",
                        expiryDate = item.expiryDate?.toString() ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private fun saveItem() {
        val currentState = _uiState.value
        if (currentState.name.isBlank() || currentState.storageLocationId.isBlank()) {
            Napier.e("Cannot save item: Name or Storage Location is not selected")
            return
        }
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true)

            var productId = currentState.productId
            if (productId.isBlank()) {
                // Create new product
                val newProduct = com.iqbalfauzi.kitchenstockmobile.domain.model.Product(
                    id = Uuid.random().toString(),
                    categoryId = currentState.categoryId.ifBlank { null },
                    name = currentState.name,
                    unit = currentState.unit
                )
                upsertProductUseCase(newProduct)
                productId = newProduct.id
            }
            
            val item = InventoryItem(
                id = currentState.id ?: Uuid.random().toString(),
                productId = productId,
                storageLocationId = currentState.storageLocationId,
                quantity = currentState.quantity.toDouble(),
                expiryDate = try { LocalDate.parse(currentState.expiryDate) } catch (_: Exception) { null },
                updatedAt = Clock.System.now(),
                product = null,
                location = null
            )
            
            upsertInventoryItemUseCase(item)
            _uiState.value = _uiState.value.copy(isSaving = false, isSuccess = true)
        }
    }

    private fun deleteItem() {
        val id = _uiState.value.id ?: return
        viewModelScope.launch {
            deleteInventoryItemUseCase(id)
            _uiState.value = _uiState.value.copy(isSuccess = true)
        }
    }
}

sealed interface InventoryDetailIntent {
    data class UpdateName(val name: String) : InventoryDetailIntent
    data class SelectProduct(val productId: String) : InventoryDetailIntent
    data class SelectCategory(val categoryId: String) : InventoryDetailIntent
    data class SelectLocation(val locationId: String) : InventoryDetailIntent
    data class UpdateQuantity(val quantity: Int) : InventoryDetailIntent
    data class UpdateUnit(val unit: String) : InventoryDetailIntent
    data class UpdateExpiryDate(val date: String) : InventoryDetailIntent
    data object Save : InventoryDetailIntent
    data object Delete : InventoryDetailIntent
    data object ResetSuccess : InventoryDetailIntent
    data object ResetForm : InventoryDetailIntent
}
