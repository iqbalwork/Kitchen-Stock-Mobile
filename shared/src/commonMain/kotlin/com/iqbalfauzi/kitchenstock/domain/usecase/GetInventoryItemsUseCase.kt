package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetInventoryItemsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<InventoryItem>> = repository.getInventoryItems()
}
