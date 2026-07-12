package com.iqbalfauzi.kitchenstock.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iqbalfauzi.kitchenstock.domain.repository.InventoryRepository
import com.iqbalfauzi.kitchenstock.domain.usecase.GetHomeSummaryUseCase
import com.iqbalfauzi.kitchenstock.presentation.home.model.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = combine(
        getHomeSummaryUseCase(),
        _isRefreshing
    ) { state, refreshing ->
        state.copy(isRefreshing = refreshing)
    }.stateIn(
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
            _isRefreshing.value = true
            try {
                inventoryRepository.syncInventory()
            } catch (_: Exception) {
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

sealed interface HomeIntent {
    data object LoadData : HomeIntent
    data object Refresh : HomeIntent
}
