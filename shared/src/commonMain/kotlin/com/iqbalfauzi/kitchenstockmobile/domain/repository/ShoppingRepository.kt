package com.iqbalfauzi.kitchenstockmobile.domain.repository

import com.iqbalfauzi.kitchenstockmobile.domain.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun getShoppingList(): Flow<List<ShoppingListItem>>
    suspend fun syncShoppingList()
    suspend fun toggleShoppingItem(id: String, isBought: Boolean)
    suspend fun addShoppingItem(productId: String, quantity: Double)
    suspend fun deleteShoppingItem(id: String)
}
