package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository

class UpsertProductUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.upsertProduct(product)
    }
}
