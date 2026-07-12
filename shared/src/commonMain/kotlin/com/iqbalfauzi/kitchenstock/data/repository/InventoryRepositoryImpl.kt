package com.iqbalfauzi.kitchenstock.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.iqbalfauzi.kitchenstock.data.mapper.toDomain
import com.iqbalfauzi.kitchenstock.data.mapper.toDto
import com.iqbalfauzi.kitchenstock.data.remote.model.CategoryDto
import com.iqbalfauzi.kitchenstock.data.remote.model.InventoryDto
import com.iqbalfauzi.kitchenstock.data.remote.model.ProductDto
import com.iqbalfauzi.kitchenstock.data.remote.model.StorageLocationDto
import com.iqbalfauzi.kitchenstock.db.KitchenDatabase
import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstock.ioDispatcher
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
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
            .mapToList(ioDispatcher)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getInventoryItemById(id: String): Flow<InventoryItem?> {
        return queries.getInventoryItemById(id)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { it?.toDomain() }
    }

    override fun getProducts(): Flow<List<Product>> {
        return queries.getAllProducts()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getStorageLocations(): Flow<List<StorageLocation>> {
        return queries.getAllStorageLocations()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getCategories(): Flow<List<com.iqbalfauzi.kitchenstock.domain.model.Category>> {
        return queries.getAllCategories()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncInventory() {
        Napier.d("Starting syncInventory...")
        withContext(ioDispatcher) {
            try {
                // 1. Fetch from Supabase
                Napier.d("Fetching categories...")
                val categories = supabase.postgrest["categories"].select().decodeList<CategoryDto>()
                Napier.d("Fetched ${categories.size} categories")

                Napier.d("Fetching storage_locations...")
                val storageLocations = supabase.postgrest["storage_locations"].select().decodeList<StorageLocationDto>()
                Napier.d("Fetched ${storageLocations.size} storage locations")

                Napier.d("Fetching products...")
                val products = supabase.postgrest["products"].select().decodeList<ProductDto>()
                Napier.d("Fetched ${products.size} products")

                Napier.d("Fetching inventory...")
                val inventory = supabase.postgrest["inventory"].select().decodeList<InventoryDto>()
                Napier.d("Fetched ${inventory.size} inventory items")

                // 2. Update Local DB
                Napier.d("Updating local database...")
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
        withContext(ioDispatcher) {
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
        withContext(ioDispatcher) {
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
        withContext(ioDispatcher) {
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
