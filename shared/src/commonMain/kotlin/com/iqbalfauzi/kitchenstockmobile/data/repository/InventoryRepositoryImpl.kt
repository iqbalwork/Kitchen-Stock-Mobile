package com.iqbalfauzi.kitchenstockmobile.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.iqbalfauzi.kitchenstockmobile.data.mapper.toDomain
import com.iqbalfauzi.kitchenstockmobile.data.mapper.toDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.CategoryDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.InventoryDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.ProductDto
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.StorageLocationDto
import com.iqbalfauzi.kitchenstockmobile.db.KitchenDatabase
import com.iqbalfauzi.kitchenstockmobile.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstockmobile.domain.model.Product
import com.iqbalfauzi.kitchenstockmobile.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
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

    override fun getInventoryItemById(id: String): Flow<InventoryItem?> {
        return queries.getInventoryItemById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override fun getProducts(): Flow<List<Product>> {
        return queries.getAllProducts()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getStorageLocations(): Flow<List<StorageLocation>> {
        return queries.getAllStorageLocations()
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
                Napier.d("Sync completed successfully")
            } catch (e: Exception) {
                Napier.e("Sync failed: ${e.message}", e)
            }
        }
    }

    override suspend fun upsertInventoryItem(item: InventoryItem) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Update Local DB
                queries.insertInventory(
                    id = item.id,
                    productId = item.productId,
                    storageLocationId = item.storageLocationId,
                    quantity = item.quantity,
                    expiryDate = item.expiryDate?.toString(),
                    updatedAt = item.updatedAt.toString()
                )

                // 2. Update Remote Supabase
                supabase.postgrest["inventory"].upsert(item.toDto())
                Napier.d("Upsert successful for item: ${item.id}")
            } catch (e: Exception) {
                Napier.e("Upsert failed: ${e.message}", e)
                // In a real app, you might want to queue this for later sync
            }
        }
    }

    override suspend fun upsertProduct(product: Product) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Update Local DB
                queries.insertProduct(
                    id = product.id,
                    categoryId = product.categoryId,
                    name = product.name,
                    barcode = product.barcode,
                    unit = product.unit,
                    minStockLevel = product.minStockLevel,
                    imageUrl = product.imageUrl
                )

                // 2. Update Remote Supabase
                val userId = supabase.auth.currentUserOrNull()?.id
                supabase.postgrest["products"].upsert(product.toDto(userId))
                Napier.d("Product upsert successful: ${product.name}")
            } catch (e: Exception) {
                Napier.e("Product upsert failed: ${e.message}", e)
            }
        }
    }

    override suspend fun deleteInventoryItem(id: String) {
        withContext(Dispatchers.IO) {
            try {
                // 1. Update Local DB
                queries.deleteInventoryById(id)

                // 2. Update Remote Supabase
                supabase.postgrest["inventory"].delete {
                    filter {
                        eq("id", id)
                    }
                }
                Napier.d("Delete successful for item: $id")
            } catch (e: Exception) {
                Napier.e("Delete failed: ${e.message}", e)
            }
        }
    }
}
