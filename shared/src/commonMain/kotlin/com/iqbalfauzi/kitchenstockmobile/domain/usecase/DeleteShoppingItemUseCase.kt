package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository

class DeleteShoppingItemUseCase(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteShoppingItem(id)
    }
}
