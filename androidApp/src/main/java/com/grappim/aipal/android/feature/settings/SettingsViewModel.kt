package com.grappim.aipal.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.model.DarkThemeConfig
import com.grappim.aipal.feature.settings.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val localDataStorage: LocalDataStorage,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SettingsState(
                onBehaviorValueChange = ::updateBehavior,
                onDarkThemeConfigClicked = ::onDarkThemeConfigClicked,
                onShowUiSettings = ::showUiSettings,
            ),
        )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.darkThemeConfig.collect { value ->
                    _state.update {
                        it.copy(darkThemeConfig = value)
                    }
                }
            }
        }
    }

    private fun showUiSettings(show: Boolean) {
        _state.update { it.copy(isUiSettingsVisible = show) }
    }

    private fun onDarkThemeConfigClicked(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            localDataStorage.setDarkThemeConfig(darkThemeConfig)
        }
    }

    private fun updateBehavior(text: String) {
        _state.update { it.copy(behavior = text) }
    }
}
