package com.iqbalfauzi.kitchenstock.presentation.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state = _state.asStateFlow()
}

data class ProfileUiState(
    val appName: String = "Kitchen Stock Manager",
    val mode: String = "Offline",
    val storageNote: String = "Semua data tersimpan lokal di perangkat ini (SQLDelight).",
    val about: String = "Kelola stok bahan dapur: jumlah, lokasi penyimpanan, dan tanggal kedaluwarsa."
)
