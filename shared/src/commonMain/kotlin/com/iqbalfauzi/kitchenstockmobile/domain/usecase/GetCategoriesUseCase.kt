package com.iqbalfauzi.kitchenstockmobile.domain.usecase

import com.iqbalfauzi.kitchenstockmobile.domain.model.Category
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val repository: InventoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getCategories()
    }
}
