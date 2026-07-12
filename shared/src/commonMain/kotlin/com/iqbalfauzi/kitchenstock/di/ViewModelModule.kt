package com.iqbalfauzi.kitchenstock.di

import com.iqbalfauzi.kitchenstock.presentation.auth.LoginViewModel
import com.iqbalfauzi.kitchenstock.presentation.home.HomeViewModel
import com.iqbalfauzi.kitchenstock.presentation.inventory_detail.InventoryDetailViewModel
import com.iqbalfauzi.kitchenstock.presentation.pantry.PantryViewModel
import com.iqbalfauzi.kitchenstock.presentation.profile.ProfileViewModel
import com.iqbalfauzi.kitchenstock.presentation.shopping.ShoppingViewModel
import com.iqbalfauzi.kitchenstock.presentation.shopping.AddShoppingItemViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PantryViewModel)
    viewModelOf(::ShoppingViewModel)
    viewModelOf(::AddShoppingItemViewModel)
    viewModelOf(::ProfileViewModel)
    viewModel { params ->
        InventoryDetailViewModel(
            getInventoryItemByIdUseCase = get(),
            getProductsUseCase = get(),
            getStorageLocationsUseCase = get(),
            upsertInventoryItemUseCase = get(),
            upsertProductUseCase = get(),
            deleteInventoryItemUseCase = get(),
            itemId = params.getOrNull(),
            getCategoriesUseCase = get()
        )
    }
}
