package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.presentation.auth.LoginViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.home.HomeViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.InventoryDetailViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.PantryViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.profile.ProfileViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.ShoppingViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.AddShoppingItemViewModel
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
