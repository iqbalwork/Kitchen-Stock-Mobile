package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository

class UpsertInventoryItemUseCase(
    private val repository: InventoryRepository
) {
    suspend operator fun invoke(item: InventoryItem) {
        repository.upsertInventoryItem(item)
    }
}
