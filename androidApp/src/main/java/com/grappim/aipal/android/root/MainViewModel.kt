package com.grappim.aipal.android.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.data.local.LocalDataStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val localDataStorage: LocalDataStorage
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            localDataStorage.darkThemeConfig.collect { value ->
                _state.update { it.copy(darkThemeConfig = value) }
            }
        }
    }
}