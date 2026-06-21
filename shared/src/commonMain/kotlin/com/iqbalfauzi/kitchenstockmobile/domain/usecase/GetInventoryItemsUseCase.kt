package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetInventoryItemsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<InventoryItem>> = repository.getInventoryItems()
}
