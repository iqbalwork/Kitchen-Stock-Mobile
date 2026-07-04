package com.iqbalfauzi.kitchenstockmobile.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.iqbalfauzi.kitchenstockmobile.data.mapper.toDomain
import com.iqbalfauzi.kitchenstockmobile.data.remote.model.ShoppingListDto
import com.iqbalfauzi.kitchenstockmobile.db.KitchenDatabase
import com.iqbalfauzi.kitchenstockmobile.domain.model.ShoppingListItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository
import io.github.aakira.napier.Napier
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ShoppingRepositoryImpl(
    private val database: KitchenDatabase,
    private val supabase: SupabaseClient
) : ShoppingRepository {

    private val queries = database.kitchenQueries

    override fun getShoppingList(): Flow<List<ShoppingListItem>> {
        return queries.getShoppingListWithDetails()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun syncShoppingList() {
        Napier.d("Starting syncShoppingList...")
        withContext(Dispatchers.IO) {
            try {
                val shoppingList = supabase.postgrest["shopping_list"].select().decodeList<ShoppingListDto>()
                Napier.d("Fetched ${shoppingList.size} shopping list items")

                database.transaction {
                    queries.deleteAllShoppingList()
                    shoppingList.forEach {
                        queries.insertShoppingList(
                            id = it.id,
                            productId = it.productId,
                            quantity = it.quantity,
                            isBought = if (it.isBought) 1L else 0L,
                            createdAt = it.createdAt,
                            updatedAt = it.updatedAt
                        )
                    }
                }
                Napier.d("Sync shopping list completed successfully")
            } catch (e: Exception) {
                Napier.e("Sync shopping list failed: ${e.message}", e)
            }
        }
    }

    override suspend fun toggleShoppingItem(id: String, isBought: Boolean) {
        withContext(Dispatchers.IO) {
            try {
                val isBoughtLong = if (isBought) 1L else 0L
                val nowStr = Clock.System.now().toString()

                // Update local DB
                queries.updateShoppingListStatus(
                    isBought = isBoughtLong,
                    updatedAt = nowStr,
                    id = id
                )

                // Update remote Supabase
                supabase.postgrest["shopping_list"].update(mapOf("is_bought" to isBought, "updated_at" to nowStr)) {
                    filter {
                        eq("id", id)
                    }
                }
                Napier.d("Toggle shopping item status successful: $id -> $isBought")
            } catch (e: Exception) {
                Napier.e("Toggle shopping item status failed: ${e.message}", e)
            }
        }
    }

    override suspend fun addShoppingItem(productId: String, quantity: Double) {
        withContext(Dispatchers.IO) {
            try {
                val userId = supabase.auth.currentUserOrNull()?.id ?: error("User not logged in")
                val itemId = Uuid.random().toString()
                val nowStr = Clock.System.now().toString()

                // Insert local DB
                queries.insertShoppingList(
                    id = itemId,
                    productId = productId,
                    quantity = quantity,
                    isBought = 0L,
                    createdAt = nowStr,
                    updatedAt = nowStr
                )

                // Insert remote Supabase
                val dto = ShoppingListDto(
                    id = itemId,
                    userId = userId,
                    productId = productId,
                    quantity = quantity,
                    isBought = false,
                    createdAt = nowStr,
                    updatedAt = nowStr
                )
                supabase.postgrest["shopping_list"].insert(dto)
                Napier.d("Add shopping item successful: $itemId")
            } catch (e: Exception) {
                Napier.e("Add shopping item failed: ${e.message}", e)
            }
        }
    }

    override suspend fun deleteShoppingItem(id: String) {
        withContext(Dispatchers.IO) {
            try {
                // Update local DB
                queries.deleteShoppingListById(id)

                // Update remote Supabase
                supabase.postgrest["shopping_list"].delete {
                    filter {
                        eq("id", id)
                    }
                }
                Napier.d("Delete shopping item successful: $id")
            } catch (e: Exception) {
                Napier.e("Delete shopping item failed: ${e.message}", e)
            }
        }
    }
}
