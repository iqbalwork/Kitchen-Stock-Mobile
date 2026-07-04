package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository

class ToggleShoppingItemUseCase(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(id: String, isBought: Boolean) {
        repository.toggleShoppingItem(id, isBought)
    }
}
