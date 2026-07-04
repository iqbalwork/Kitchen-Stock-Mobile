package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetCategoriesUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertProductUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.AddShoppingItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class AddShoppingItemUiState(
    val productId: String = "",
    val name: String = "",
    val categoryId: String = "",
    val quantity: Double = 1.0,
    val unit: String = "Units",
    val products: List<Product> = emptyList(),
    val categories: List<com.iqbalfauzi.kitchenstockmobile.domain.model.Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AddShoppingItemIntent {
    data class UpdateName(val name: String) : AddShoppingItemIntent
    data class SelectProduct(val productId: String) : AddShoppingItemIntent
    data class SelectCategory(val categoryId: String) : AddShoppingItemIntent
    data class UpdateQuantity(val quantity: Double) : AddShoppingItemIntent
    data class UpdateUnit(val unit: String) : AddShoppingItemIntent
    data object Save : AddShoppingItemIntent
    data object ResetSuccess : AddShoppingItemIntent
}

class AddShoppingItemViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val upsertProductUseCase: UpsertProductUseCase,
    private val addShoppingItemUseCase: AddShoppingItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddShoppingItemUiState())
    val uiState: StateFlow<AddShoppingItemUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            getProductsUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to load products"
                    )
                }
                .collect { productsList ->
                    _uiState.value = _uiState.value.copy(
                        products = productsList,
                        isLoading = false
                    )
                }
        }
        viewModelScope.launch {
            getCategoriesUseCase().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun onIntent(intent: AddShoppingItemIntent) {
        when (intent) {
            is AddShoppingItemIntent.UpdateName -> {
                val exactMatch = _uiState.value.products.find { it.name.equals(intent.name, ignoreCase = true) }
                _uiState.value = _uiState.value.copy(
                    name = intent.name,
                    productId = exactMatch?.id ?: "",
                    categoryId = exactMatch?.categoryId ?: _uiState.value.categoryId
                )
            }
            is AddShoppingItemIntent.SelectProduct -> {
                val product = _uiState.value.products.find { it.id == intent.productId }
                _uiState.value = _uiState.value.copy(
                    productId = intent.productId,
                    name = product?.name ?: "",
                    unit = product?.unit ?: "Units",
                    categoryId = product?.categoryId ?: _uiState.value.categoryId
                )
            }
            is AddShoppingItemIntent.SelectCategory -> {
                _uiState.value = _uiState.value.copy(categoryId = intent.categoryId)
            }
            is AddShoppingItemIntent.UpdateQuantity -> {
                _uiState.value = _uiState.value.copy(quantity = intent.quantity)
            }
            is AddShoppingItemIntent.UpdateUnit -> {
                _uiState.value = _uiState.value.copy(unit = intent.unit)
            }
            AddShoppingItemIntent.Save -> saveItem()
            AddShoppingItemIntent.ResetSuccess -> _uiState.value = _uiState.value.copy(isSuccess = false)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveItem() {
        val currentState = _uiState.value
        if (currentState.name.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                var productId = currentState.productId
                if (productId.isBlank()) {
                    val newProduct = Product(
                        id = Uuid.random().toString(),
                        categoryId = currentState.categoryId.ifBlank { null },
                        name = currentState.name,
                        unit = currentState.unit
                    )
                    upsertProductUseCase(newProduct)
                    productId = newProduct.id
                }

                addShoppingItemUseCase(
                    productId = productId,
                    quantity = currentState.quantity
                )
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to add item"
                )
            }
        }
    }
}
