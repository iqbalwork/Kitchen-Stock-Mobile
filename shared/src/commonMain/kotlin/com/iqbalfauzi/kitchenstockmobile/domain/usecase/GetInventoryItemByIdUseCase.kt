package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetInventoryItemByIdUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(id: String): Flow<InventoryItem?> {
        return repository.getInventoryItemById(id)
    }
}
