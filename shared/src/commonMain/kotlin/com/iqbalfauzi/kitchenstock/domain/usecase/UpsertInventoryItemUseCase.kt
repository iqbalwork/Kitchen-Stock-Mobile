package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository

class UpsertInventoryItemUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(item: InventoryItem) {
        repository.upsertInventoryItem(item)
    }
}
