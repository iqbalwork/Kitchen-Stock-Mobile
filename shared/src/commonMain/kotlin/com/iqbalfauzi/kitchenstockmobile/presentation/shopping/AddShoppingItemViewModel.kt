package com.iqbalfauzi.kitchenstockmobile.presentation.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.AddShoppingItemUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class AddShoppingItemUiState(
    val products: List<Product> = emptyList(),
    val selectedProduct: Product? = null,
    val quantity: String = "1",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddShoppingItemViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val addShoppingItemUseCase: AddShoppingItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddShoppingItemUiState())
    val uiState: StateFlow<AddShoppingItemUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
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
                        selectedProduct = productsList.firstOrNull(),
                        isLoading = false
                    )
                }
        }
    }

    fun selectProduct(product: Product?) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun updateQuantity(quantity: String) {
        _uiState.value = _uiState.value.copy(quantity = quantity)
    }

    fun addShoppingItem() {
        val currentState = _uiState.value
        val product = currentState.selectedProduct ?: return
        val qtyVal = currentState.quantity.toDoubleOrNull() ?: 1.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                addShoppingItemUseCase(
                    productId = product.id,
                    quantity = qtyVal
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to add item"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AddShoppingItemUiState(
            products = _uiState.value.products,
            selectedProduct = _uiState.value.products.firstOrNull(),
            quantity = "1",
            isLoading = false,
            isSuccess = false,
            errorMessage = null
        )
    }
}
