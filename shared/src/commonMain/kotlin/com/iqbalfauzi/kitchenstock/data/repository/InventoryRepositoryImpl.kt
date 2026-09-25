package com.iqbalfauzi.kitchenstock.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.iqbalfauzi.kitchenstock.data.mapper.toDomain
import com.iqbalfauzi.kitchenstock.db.KitchenDatabase
import com.iqbalfauzi.kitchenstock.domain.model.InventoryItem
import com.iqbalfauzi.kitchenstock.domain.model.Product
import com.iqbalfauzi.kitchenstock.domain.model.StorageLocation
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstock.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class InventoryRepositoryImpl(
    private val database: KitchenDatabase
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

    override suspend fun upsertInventoryItem(item: InventoryItem) {
        withContext(ioDispatcher) {
            queries.insertInventory(
                id = item.id,
                productId = item.productId,
                storageLocationId = item.storageLocationId,
                quantity = item.quantity,
                expiryDate = item.expiryDate?.toString(),
                updatedAt = item.updatedAt.toString()
            )
        }
    }

    override suspend fun upsertProduct(product: Product) {
        withContext(ioDispatcher) {
            queries.insertProduct(
                id = product.id,
                categoryId = product.categoryId,
                name = product.name,
                barcode = product.barcode,
                unit = product.unit,
                minStockLevel = product.minStockLevel,
                imageUrl = product.imageUrl
            )
        }
    }

    override suspend fun deleteInventoryItem(id: String) {
        withContext(ioDispatcher) {
            queries.deleteInventoryById(id)
        }
    }
}
