package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository

class AddShoppingItemUseCase(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(productId: String, quantity: Double) {
        repository.addShoppingItem(productId, quantity)
    }
}
