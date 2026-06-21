package com.iqbalfauzi.kitchenstockmobile.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.iqbalfauzi.kitchenstockmobile.data.mapper.toDomain
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.CategoryDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.InventoryDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.ProductDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.StorageLocationDto
import com.iqbalfauzi.kitchenstockmobile.db.KitchenDatabase
import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class InventoryRepositoryImpl(
    private val database: KitchenDatabase,
    private val supabase: SupabaseClient
) : InventoryRepository {

    private val queries = database.kitchenQueries

    override fun getInventoryItems(): Flow<List<InventoryItem>> {
        return queries.getInventoryWithDetails()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncInventory() {
        withContext(Dispatchers.IO) {
            try {
                // 1. Fetch from Supabase
                val categories = supabase.postgrest["categories"].select().decodeList<CategoryDto>()
                val storageLocations = supabase.postgrest["storage_locations"].select().decodeList<StorageLocationDto>()
                val products = supabase.postgrest["products"].select().decodeList<ProductDto>()
                val inventory = supabase.postgrest["inventory"].select().decodeList<InventoryDto>()

                // 2. Update Local DB
                database.transaction {
                    queries.deleteAllInventory()
                    queries.deleteAllProducts()
                    queries.deleteAllStorageLocations()
                    queries.deleteAllCategories()

                    categories.forEach {
                        queries.insertCategory(it.id, it.name, it.icon)
                    }
                    storageLocations.forEach {
                        queries.insertStorageLocation(it.id, it.name, it.description)
                    }
                    products.forEach {
                        queries.insertProduct(it.id, it.categoryId, it.name, it.barcode, it.unit, it.minStockLevel, it.imageUrl)
                    }
                    inventory.forEach {
                        queries.insertInventory(it.id, it.productId, it.storageLocationId, it.quantity, it.expiryDate, it.updatedAt)
                    }
                }
                println("Sync completed successfully")
            } catch (e: Exception) {
                println("Sync failed: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
