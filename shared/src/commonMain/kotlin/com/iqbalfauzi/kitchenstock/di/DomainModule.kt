package com.iqbalfauzi.kitchenstock.di

import com.iqbalfauzi.kitchenstock.domain.usecase.DeleteInventoryItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetCategoriesUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetHomeSummaryUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetInventoryItemByIdUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetInventoryItemsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.UpsertProductUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.GetShoppingListUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.ToggleShoppingItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.AddShoppingItemUseCase
import com.iqbalfauzi.kitchenstock.domain.usecase.DeleteShoppingItemUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetHomeSummaryUseCase)
    factoryOf(::GetInventoryItemByIdUseCase)
    factoryOf(::GetInventoryItemsUseCase)
    factoryOf(::GetProductsUseCase)
    factoryOf(::GetStorageLocationsUseCase)
    factoryOf(::GetCategoriesUseCase)
    factoryOf(::UpsertInventoryItemUseCase)
    factoryOf(::UpsertProductUseCase)
    factoryOf(::DeleteInventoryItemUseCase)
    factoryOf(::GetShoppingListUseCase)
    factoryOf(::ToggleShoppingItemUseCase)
    factoryOf(::AddShoppingItemUseCase)
    factoryOf(::DeleteShoppingItemUseCase)
}

