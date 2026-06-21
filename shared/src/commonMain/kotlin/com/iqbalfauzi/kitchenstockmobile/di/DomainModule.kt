package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.domain.usecase.DeleteInventoryItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetCategoriesUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetHomeSummaryUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetInventoryItemByIdUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetInventoryItemsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetProductsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetStorageLocationsUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertInventoryItemUseCase
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.UpsertProductUseCase
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
}
