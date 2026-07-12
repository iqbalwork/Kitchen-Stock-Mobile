package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.repository.ShoppingRepository

class DeleteShoppingItemUseCase(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteShoppingItem(id)
    }
}
