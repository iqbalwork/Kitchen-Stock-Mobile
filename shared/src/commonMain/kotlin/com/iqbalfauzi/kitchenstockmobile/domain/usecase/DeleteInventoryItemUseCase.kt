package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository

class DeleteInventoryItemUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteInventoryItem(id)
    }
}
