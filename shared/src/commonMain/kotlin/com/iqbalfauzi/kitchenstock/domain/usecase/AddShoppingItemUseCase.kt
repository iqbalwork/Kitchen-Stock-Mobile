package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.repository.ShoppingRepository

class AddShoppingItemUseCase(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(productId: String, quantity: Double) {
        repository.addShoppingItem(productId, quantity)
    }
}
