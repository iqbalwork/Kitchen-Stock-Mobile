package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.ShoppingListItem
import com.iqbalfauzi.kitchenstock.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow

class GetShoppingListUseCase(
    private val repository: ShoppingRepository
) {
    operator fun invoke(): Flow<List<ShoppingListItem>> = repository.getShoppingList()
}
