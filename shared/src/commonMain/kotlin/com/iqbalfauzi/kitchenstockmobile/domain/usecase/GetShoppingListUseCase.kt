package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.ShoppingListItem
import com.iqbalfauzi.kitchenstockmobile.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow

class GetShoppingListUseCase(
    private val repository: ShoppingRepository
) {
    operator fun invoke(): Flow<List<ShoppingListItem>> = repository.getShoppingList()
}
