package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetInventoryItemByIdUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(id: String): Flow<InventoryItem?> {
        return repository.getInventoryItemById(id)
    }
}
