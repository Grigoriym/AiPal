package com.grappim.aipal.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.data.local.DarkThemeConfig
import com.grappim.aipal.android.data.local.LocalDataStorage
import com.grappim.aipal.android.data.repo.AiPalRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val aiPalRepo: AiPalRepo,
    private val localDataStorage: LocalDataStorage,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SettingsState(
                onBehaviorValueChange = ::updateBehavior,
                onSetBehavior = ::setBehavior,
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

    private fun setBehavior() {
        viewModelScope.launch {
            aiPalRepo.setBehavior(state.value.behavior)
        }
    }
}
