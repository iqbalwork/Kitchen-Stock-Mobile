package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetStorageLocationsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<StorageLocation>> {
        return repository.getStorageLocations()
    }
}
