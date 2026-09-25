package com.iqbalfauzi.kitchenstock.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.domain.usecase.AddShoppingItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.DeleteShoppingItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetInventoryItemsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetShoppingListUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.ToggleShoppingItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.UpsertProductUseCase
import com.iqbalfauzi.kitchenstock.presentation.shopping.model.ShoppingBadgeType
import com.iqbalfauzi.kitchenstock.presentation.shopping.model.ShoppingItem
import com.iqbalfauzi.kitchenstock.presentation.shopping.model.ShoppingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface ShoppingIntent {
    data class UpdateQuickAddText(val text: String) : ShoppingIntent
    data object QuickAddCurrentItem : ShoppingIntent
    data class ToggleItem(val id: String) : ShoppingIntent
    data class DeleteItem(val id: String) : ShoppingIntent
    data object CompleteShopping : ShoppingIntent
    data class RestockSingleItem(val id: String) : ShoppingIntent
    data object ToggleBoughtExpanded : ShoppingIntent
    data object ClearAllBought : ShoppingIntent
    data object Refresh : ShoppingIntent
    data object DismissUserMessage : ShoppingIntent
}

class ShoppingViewModel(
    private val getShoppingListUseCase: GetShoppingListUseCase,
    private val toggleShoppingItemUseCase: ToggleShoppingItemUseCase,
    private val deleteShoppingItemUseCase: DeleteShoppingItemUseCase,
    private val upsertInventoryItemUseCase: UpsertInventoryItemUseCase,
    private val getStorageLocationsUseCase: GetStorageLocationsUseCase,
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val upsertProductUseCase: UpsertProductUseCase,
    private val addShoppingItemUseCase: AddShoppingItemUseCase
) : ViewModel() {

    private val _quickAddInput = MutableStateFlow("")
    private val _isBoughtExpanded = MutableStateFlow(true)
    private val _isRefreshing = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)
    private val _storageLocations = MutableStateFlow<List<StorageLocation>>(emptyList())

    val uiState: StateFlow<ShoppingUiState> = combine(
        getShoppingListUseCase(),
        getInventoryItemsUseCase(),
        _quickAddInput,
        _isBoughtExpanded,
        _isRefreshing
    ) { items, inventoryItems, quickInput, isExpanded, refreshing ->
        val pendingList = mutableListOf<ShoppingItem>()
        val boughtList = mutableListOf<ShoppingItem>()

        items.forEach { item ->
            val rawQty = item.quantity
            val qtyDisplay = if (rawQty % 1.0 == 0.0) rawQty.toInt().toString() else rawQty.toString()
            val unit = item.product?.unit ?: "pcs"
            val formattedQty = "$qtyDisplay $unit"
            val name = item.product?.name ?: "Bahan Makanan"

            if (!item.isBought) {
                val invItems = inventoryItems.filter { it.productId == item.productId }
                val (badgeText, badgeType) = if (invItems.isNotEmpty()) {
                    val totalQty = invItems.sumOf { it.quantity }
                    val locName = invItems.firstOrNull()?.location?.name ?: "Pantry"
                    if (totalQty <= 0.0) {
                        "Stok Habis di $locName" to ShoppingBadgeType.Danger
                    } else if (totalQty <= (item.product?.minStockLevel ?: 1.0)) {
                        "Stok Menipis di $locName" to ShoppingBadgeType.Warning
                    } else {
                        locName to ShoppingBadgeType.Neutral
                    }
                } else if (!item.product?.category?.name.isNullOrBlank()) {
                    item.product.category.name to ShoppingBadgeType.Neutral
                } else {
                    null to ShoppingBadgeType.Neutral
                }

                pendingList.add(
                    ShoppingItem(
                        id = item.id,
                        productId = item.productId,
                        name = name,
                        quantity = formattedQty,
                        rawQuantity = rawQty,
                        unit = unit,
                        isChecked = false,
                        badgeText = badgeText,
                        badgeType = badgeType
                    )
                )
            } else {
                boughtList.add(
                    ShoppingItem(
                        id = item.id,
                        productId = item.productId,
                        name = name,
                        quantity = formattedQty,
                        rawQuantity = rawQty,
                        unit = unit,
                        isChecked = true,
                        badgeText = "Restock otomatis ke Pantry",
                        badgeType = ShoppingBadgeType.Restock
                    )
                )
            }
        }

        ShoppingUiState(
            quickAddInput = quickInput,
            pendingItems = pendingList,
            boughtItems = boughtList,
            isBoughtExpanded = isExpanded,
            isRefreshing = refreshing
        )
    }.combine(_userMessage) { state, message ->
        state.copy(userMessage = message)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ShoppingUiState()
    )

    init {
        loadStorageLocations()
    }

    private fun loadStorageLocations() {
        viewModelScope.launch {
            getStorageLocationsUseCase().collect {
                _storageLocations.value = it
            }
        }
    }

    fun onIntent(intent: ShoppingIntent) {
        when (intent) {
            is ShoppingIntent.UpdateQuickAddText -> {
                _quickAddInput.value = intent.text
            }
            ShoppingIntent.QuickAddCurrentItem -> quickAddCurrentItem()
            is ShoppingIntent.ToggleItem -> toggleItem(intent.id)
            is ShoppingIntent.DeleteItem -> deleteItem(intent.id)
            ShoppingIntent.CompleteShopping -> completeShopping()
            is ShoppingIntent.RestockSingleItem -> restockSingleItem(intent.id)
            ShoppingIntent.ToggleBoughtExpanded -> {
                _isBoughtExpanded.value = !_isBoughtExpanded.value
            }
            ShoppingIntent.ClearAllBought -> clearAllBought()
            ShoppingIntent.Refresh -> syncData()
            ShoppingIntent.DismissUserMessage -> {
                _userMessage.value = null
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun quickAddCurrentItem() {
        val text = _quickAddInput.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            try {
                val products = getProductsUseCase().first()
                val existingProduct = products.find { it.name.equals(text, ignoreCase = true) }
                val productId = if (existingProduct != null) {
                    existingProduct.id
                } else {
                    val newProduct = Product(
                        id = Uuid.random().toString(),
                        categoryId = null,
                        name = text,
                        unit = "pcs"
                    )
                    upsertProductUseCase(newProduct)
                    newProduct.id
                }

                addShoppingItemUseCase(productId = productId, quantity = 1.0)
                _quickAddInput.value = ""
            } catch (e: Exception) {
                _userMessage.value = "Gagal menambah belanjaan: ${e.message}"
            }
        }
    }

    private fun toggleItem(id: String) {
        viewModelScope.launch {
            val currentShopping = getShoppingListUseCase().first()
            val item = currentShopping.find { it.id == id } ?: return@launch
            toggleShoppingItemUseCase(id, !item.isBought)
        }
    }

    private fun deleteItem(id: String) {
        viewModelScope.launch {
            deleteShoppingItemUseCase(id)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun completeShopping() {
        viewModelScope.launch {
            try {
                val currentShopping = getShoppingListUseCase().first()
                val bought = currentShopping.filter { it.isBought }
                if (bought.isEmpty()) return@launch

                val inventoryItems = getInventoryItemsUseCase().first()
                val locations = _storageLocations.value.ifEmpty {
                    getStorageLocationsUseCase().first()
                }
                val defaultLocation = locations.find {
                    it.name.contains("Lemari Pantry", ignoreCase = true)
                } ?: locations.firstOrNull()

                bought.forEach { shoppingItem ->
                    val existingInv = inventoryItems.find { it.productId == shoppingItem.productId }
                    if (existingInv != null) {
                        upsertInventoryItemUseCase(
                            existingInv.copy(
                                quantity = existingInv.quantity + shoppingItem.quantity,
                                updatedAt = Clock.System.now()
                            )
                        )
                    } else if (defaultLocation != null) {
                        val newInv = InventoryItem(
                            id = Uuid.random().toString(),
                            productId = shoppingItem.productId,
                            storageLocationId = defaultLocation.id,
                            quantity = shoppingItem.quantity,
                            expiryDate = null,
                            updatedAt = Clock.System.now(),
                            product = null,
                            location = null
                        )
                        upsertInventoryItemUseCase(newInv)
                    }
                    deleteShoppingItemUseCase(shoppingItem.id)
                }
                _userMessage.value = "${bought.size} item berhasil direstock ke Pantry!"
            } catch (e: Exception) {
                _userMessage.value = "Gagal menyelesaikan belanja: ${e.message}"
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun restockSingleItem(id: String) {
        viewModelScope.launch {
            try {
                val shoppingItem = getShoppingListUseCase().first().find { it.id == id } ?: return@launch
                val inventoryItems = getInventoryItemsUseCase().first()
                val locations = _storageLocations.value.ifEmpty {
                    getStorageLocationsUseCase().first()
                }
                val defaultLocation = locations.find {
                    it.name.contains("Lemari Pantry", ignoreCase = true)
                } ?: locations.firstOrNull()

                val existingInv = inventoryItems.find { it.productId == shoppingItem.productId }
                if (existingInv != null) {
                    upsertInventoryItemUseCase(
                        existingInv.copy(
                            quantity = existingInv.quantity + shoppingItem.quantity,
                            updatedAt = Clock.System.now()
                        )
                    )
                } else if (defaultLocation != null) {
                    val newInv = InventoryItem(
                        id = Uuid.random().toString(),
                        productId = shoppingItem.productId,
                        storageLocationId = defaultLocation.id,
                        quantity = shoppingItem.quantity,
                        expiryDate = null,
                        updatedAt = Clock.System.now(),
                        product = null,
                        location = null
                    )
                    upsertInventoryItemUseCase(newInv)
                }
                deleteShoppingItemUseCase(id)
                _userMessage.value = "${shoppingItem.product?.name ?: "Item"} direstock ke Pantry!"
            } catch (e: Exception) {
                _userMessage.value = "Gagal restock item: ${e.message}"
            }
        }
    }

    private fun clearAllBought() {
        viewModelScope.launch {
            val currentShopping = getShoppingListUseCase().first()
            val bought = currentShopping.filter { it.isBought }
            bought.forEach {
                deleteShoppingItemUseCase(it.id)
            }
            if (bought.isNotEmpty()) {
                _userMessage.value = "${bought.size} item dibersihkan dari daftar"
            }
        }
    }

    private fun syncData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            yield()
            _isRefreshing.value = false
        }
    }
}
