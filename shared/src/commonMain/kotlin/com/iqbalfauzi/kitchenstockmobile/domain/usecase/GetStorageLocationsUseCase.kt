package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetStorageLocationsUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<StorageLocation>> {
        return repository.getStorageLocations()
    }
}
