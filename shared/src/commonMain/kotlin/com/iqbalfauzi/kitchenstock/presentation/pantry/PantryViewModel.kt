package com.iqbalfauzi.kitchenstock.presentation.pantry

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Kitchen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.domain.usecase.GetCategoriesUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetInventoryItemsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.UpsertProductUseCase
import com.iqbalfauzi.kitchenstock.presentation.pantry.model.ExpiryStatus
import com.iqbalfauzi.kitchenstock.presentation.pantry.model.PantryItem
import com.iqbalfauzi.kitchenstock.presentation.pantry.model.PantryUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PantryViewModel(
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val getStorageLocationsUseCase: GetStorageLocationsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val upsertInventoryItemUseCase: UpsertInventoryItemUseCase,
    private val upsertProductUseCase: UpsertProductUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PantryUiState())
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _isAddSheetVisible = MutableStateFlow(false)
    private val _isRefreshing = MutableStateFlow(false)

    private var allItems: List<InventoryItem> = emptyList()
    private var allProducts: List<Product> = emptyList()

    private data class FilterState(
        val query: String,
        val selectedLocationId: String?,
        val isAddSheet: Boolean,
        val isRefreshing: Boolean
    )

    init {
        observeData()
    }

    private fun observeData() {
        val filtersFlow = combine(
            _searchQuery,
            _selectedCategoryId,
            _isAddSheetVisible,
            _isRefreshing
        ) { query, selectedId, isAddSheet, isRefreshing ->
            FilterState(query, selectedId, isAddSheet, isRefreshing)
        }

        viewModelScope.launch {
            combine(
                getInventoryItemsUseCase(),
                getStorageLocationsUseCase(),
                getCategoriesUseCase(),
                getProductsUseCase(),
                filtersFlow
            ) { items, locations, categories, products, filters ->
                allItems = items
                allProducts = products

                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

                // Agregasi metriks reaktif dari seluruh bahan aktif
                val expiredCount = items.count { it.expiryDate != null && it.expiryDate <= today }
                val nearExpiryCount = items.count {
                    it.expiryDate != null && it.expiryDate > today && today.daysUntil(it.expiryDate) <= 3
                }
                val totalStockCount = items.size

                // Filter lokasi
                val locationFiltered = if (filters.selectedLocationId == null) {
                    items
                } else {
                    items.filter { it.storageLocationId == filters.selectedLocationId }
                }

                // Filter pencarian instan
                val searchFiltered = if (filters.query.isBlank()) {
                    locationFiltered
                } else {
                    val q = filters.query.trim()
                    locationFiltered.filter { item ->
                        val nameMatch = item.product?.name?.contains(q, ignoreCase = true) == true
                        val catMatch = item.product?.category?.name?.contains(q, ignoreCase = true) == true
                        val locMatch = item.location?.name?.contains(q, ignoreCase = true) == true
                        nameMatch || catMatch || locMatch
                    }
                }

                val pantryItems = searchFiltered.map { it.toPantryItem(today) }

                PantryUiState(
                    categories = locations,
                    selectedCategoryId = filters.selectedLocationId,
                    groupedItems = pantryItems.groupBy { it.location.uppercase() },
                    items = pantryItems,
                    isRefreshing = filters.isRefreshing,
                    searchQuery = filters.query,
                    expiredCount = expiredCount,
                    nearExpiryCount = nearExpiryCount,
                    totalStockCount = totalStockCount,
                    isAddSheetVisible = filters.isAddSheet,
                    storageLocations = locations,
                    availableCategories = categories
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onIntent(intent: PantryIntent) {
        when (intent) {
            is PantryIntent.SelectCategory -> {
                _selectedCategoryId.value = intent.categoryId
            }
            is PantryIntent.Search -> {
                _searchQuery.value = intent.query
            }
            is PantryIntent.ShowAddSheet -> {
                _isAddSheetVisible.value = intent.show
            }
            is PantryIntent.ChangeQuantity -> {
                val item = allItems.find { it.id == intent.id } ?: return
                val newQty = maxOf(0, item.quantity.toInt() + intent.delta)
                updateQuantity(intent.id, newQty)
            }
            is PantryIntent.UpdateQuantity -> {
                updateQuantity(intent.id, intent.newQuantity)
            }
            is PantryIntent.QuickAddIngredient -> {
                quickAddIngredient(intent)
            }
            PantryIntent.Refresh -> refresh()
        }
    }

    /** Data mengalir reaktif dari SQLDelight; refresh cuma memutar indikator. */
    private fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            yield()
            _isRefreshing.value = false
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

    @OptIn(ExperimentalUuidApi::class)
    private fun quickAddIngredient(intent: PantryIntent.QuickAddIngredient) {
        if (intent.name.isBlank()) return
        viewModelScope.launch {
            val trimmedName = intent.name.trim()
            val existingProduct = allProducts.find { it.name.equals(trimmedName, ignoreCase = true) }
            val productId = existingProduct?.id ?: Uuid.random().toString()

            val productToUpsert = Product(
                id = productId,
                name = trimmedName,
                unit = intent.unit,
                categoryId = intent.categoryId ?: existingProduct?.categoryId,
                minStockLevel = existingProduct?.minStockLevel ?: 1.0
            )
            upsertProductUseCase(productToUpsert)

            val targetLocationId = intent.storageLocationId.ifBlank {
                _uiState.value.storageLocations.firstOrNull()?.id ?: "loc-kulkas"
            }

            val item = InventoryItem(
                id = Uuid.random().toString(),
                productId = productId,
                storageLocationId = targetLocationId,
                quantity = intent.quantity,
                expiryDate = intent.expiryDate,
                updatedAt = Clock.System.now(),
                product = null,
                location = null
            )
            upsertInventoryItemUseCase(item)

            if (!intent.keepOpen) {
                _isAddSheetVisible.value = false
            }
        }
    }

    private fun InventoryItem.toPantryItem(today: LocalDate): PantryItem {
        val status = when {
            expiryDate == null -> ExpiryStatus.Safe("Aman")
            expiryDate <= today -> {
                val daysAgo = expiryDate.daysUntil(today)
                val label = when (daysAgo) {
                    0 -> "Expired Hari Ini"
                    1 -> "Expired Kemarin"
                    else -> "Expired ($daysAgo hari lalu)"
                }
                ExpiryStatus.Expired(label)
            }
            today.daysUntil(expiryDate) <= 3 -> {
                val daysLeft = today.daysUntil(expiryDate)
                val label = if (daysLeft == 1) "1 hari lagi" else "$daysLeft hari lagi"
                ExpiryStatus.NearExpiry(label)
            }
            else -> {
                val daysLeft = today.daysUntil(expiryDate)
                ExpiryStatus.Safe("Aman ($daysLeft hari)")
            }
        }

        val locationName = location?.name ?: "Pantry"
        val icon = when {
            locationName.contains("Kulkas", ignoreCase = true) ||
                locationName.contains("Fridge", ignoreCase = true) -> Icons.Default.Kitchen
            locationName.contains("Freezer", ignoreCase = true) -> Icons.Default.AcUnit
            locationName.contains("Bumbu", ignoreCase = true) -> Icons.Default.Coffee
            else -> Icons.Default.Inventory2
        }

        return PantryItem(
            id = id,
            name = product?.name ?: "Unknown",
            quantity = quantity.toInt(),
            unit = product?.unit ?: "pcs",
            location = locationName,
            category = product?.category?.name ?: "",
            icon = icon,
            expiryStatus = status
        )
    }
}

sealed interface PantryIntent {
    data class SelectCategory(val categoryId: String?) : PantryIntent
    data class UpdateQuantity(val id: String, val newQuantity: Int) : PantryIntent
    data class ChangeQuantity(val id: String, val delta: Int) : PantryIntent
    data class Search(val query: String) : PantryIntent
    data class ShowAddSheet(val show: Boolean) : PantryIntent
    data class QuickAddIngredient(
        val name: String,
        val quantity: Double = 1.0,
        val unit: String = "pcs",
        val storageLocationId: String,
        val expiryDate: LocalDate? = null,
        val categoryId: String? = null,
        val keepOpen: Boolean = false
    ) : PantryIntent
    data object Refresh : PantryIntent
}

