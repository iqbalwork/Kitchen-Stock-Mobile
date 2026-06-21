package com.iqbalfauzi.kitchenstockmobile.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstockmobile.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetHomeSummaryUseCase
import com.iqbalfauzi.kitchenstockmobile.presentation.home.model.HomeUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = getHomeSummaryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    init {
        refresh()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadData -> refresh()
            HomeIntent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            inventoryRepository.syncInventory()
        }
    }
}

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data object Refresh : HomeIntent
}
