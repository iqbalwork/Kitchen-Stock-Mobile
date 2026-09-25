package com.iqbalfauzi.kitchenstock.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.iqbalfauzi.kitchenstock.data.mapper.toDomain
import com.iqbalfauzi.kitchenstock.db.KitchenDatabase
import com.iqbalfauzi.kitchenstock.domain.model.ShoppingListItem
import com.iqbalfauzi.kitchenstock.domain.repository.ShoppingRepository
import com.iqbalfauzi.kitchenstock.ioDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ShoppingRepositoryImpl(
    private val database: KitchenDatabase
) : ShoppingRepository {

    private val queries = database.kitchenQueries

    override fun getShoppingList(): Flow<List<ShoppingListItem>> {
        return queries.getShoppingListWithDetails()
            .asFlow()
            .mapToList(ioDispatcher)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun toggleShoppingItem(id: String, isBought: Boolean) {
        withContext(ioDispatcher) {
            queries.updateShoppingListStatus(
                isBought = if (isBought) 1L else 0L,
                updatedAt = Clock.System.now().toString(),
                id = id
            )
        }
    }

    override suspend fun addShoppingItem(productId: String, quantity: Double) {
        withContext(ioDispatcher) {
            val nowStr = Clock.System.now().toString()
            queries.insertShoppingList(
                id = Uuid.random().toString(),
                productId = productId,
                quantity = quantity,
                isBought = 0L,
                createdAt = nowStr,
                updatedAt = nowStr
            )
        }
    }

    override suspend fun deleteShoppingItem(id: String) {
        withContext(ioDispatcher) {
            queries.deleteShoppingListById(id)
        }
    }
}
