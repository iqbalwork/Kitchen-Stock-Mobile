package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetProductsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return repository.getProducts()
    }
}
