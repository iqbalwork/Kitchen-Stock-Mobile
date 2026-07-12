package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.repository.ShoppingRepository

class ToggleShoppingItemUseCase(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(id: String, isBought: Boolean) {
        repository.toggleShoppingItem(id, isBought)
    }
}
