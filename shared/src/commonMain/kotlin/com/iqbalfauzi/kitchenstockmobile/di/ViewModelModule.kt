package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.presentation.home.HomeViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.inventory_detail.InventoryDetailViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.pantry.PantryViewModel
import com.iqbalfauzi.kitchenstockmobile.presentation.shopping.ShoppingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::PantryViewModel)
    viewModelOf(::ShoppingViewModel)
    viewModel { params -> InventoryDetailViewModel(itemId = params.getOrNull()) }
}
