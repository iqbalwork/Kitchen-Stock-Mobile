package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository

class UpsertProductUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(product: Product) {
        repository.upsertProduct(product)
    }
}
