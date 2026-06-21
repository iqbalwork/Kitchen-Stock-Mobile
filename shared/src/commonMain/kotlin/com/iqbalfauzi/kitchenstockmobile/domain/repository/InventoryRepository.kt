package com.iqbalfauzi.kitchenstockmobile.domain.repository

import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventoryItems(): Flow<List<InventoryItem>>
    suspend fun syncInventory()
}
