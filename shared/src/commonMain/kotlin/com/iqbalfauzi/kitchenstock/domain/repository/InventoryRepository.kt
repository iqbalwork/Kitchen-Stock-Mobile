package com.iqbalfauzi.kitchenstock.domain.repository

import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    fun getInventoryItems(): Flow<List<InventoryItem>>
    fun getInventoryItemById(id: String): Flow<InventoryItem?>
    fun getProducts(): Flow<List<Product>>
    fun getStorageLocations(): Flow<List<StorageLocation>>
    fun getCategories(): Flow<List<Category>>
    suspend fun syncInventory()
    suspend fun upsertInventoryItem(item: InventoryItem)
    suspend fun upsertProduct(product: Product)
    suspend fun deleteInventoryItem(id: String)
}
