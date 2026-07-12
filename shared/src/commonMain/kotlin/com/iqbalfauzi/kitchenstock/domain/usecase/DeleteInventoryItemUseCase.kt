package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository

class DeleteInventoryItemUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteInventoryItem(id)
    }
}
