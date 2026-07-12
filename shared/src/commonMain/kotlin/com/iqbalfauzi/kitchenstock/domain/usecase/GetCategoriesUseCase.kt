package com.iqbalfauzi.kitchenstock.domain.usecase

import com.iqbalfauzi.kitchenstock.domain.model.Category
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getCategories()
    }
}
