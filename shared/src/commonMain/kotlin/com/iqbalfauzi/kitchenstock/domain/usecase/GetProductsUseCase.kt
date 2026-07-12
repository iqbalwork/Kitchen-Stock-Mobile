package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getProducts()
    }
}
