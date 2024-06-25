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
import org.lighthousegames.logging.logging

class SettingsViewModel(
    private val aiPalRepo: AiPalRepo,
    private val localDataStorage: LocalDataStorage,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SettingsState(
                onSetTempValue = ::setTemp,
                applySettings = ::applyChanges,
                onGetModels = ::getModels,
                onSetModel = ::setModel,
                onBehaviorValueChange = ::updateBehavior,
                onSetBehavior = ::setBehavior,
                onDarkThemeConfigClicked = ::onDarkThemeConfigClicked,
                onShowUiSettings = ::showUiSettings
            ),
        )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.tempFlow.collect {
                    setTemp(it)
                }
            }
            launch {
                localDataStorage.currentGptModel.collect { value ->
                    _state.update { it.copy(selectedModel = value) }
                }
            }
            launch {
                localDataStorage.gptModels.collect { value ->
                    _state.update { it.copy(models = value) }
                }
            }
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

    private fun getModels() {
        viewModelScope.launch {
            val models = aiPalRepo.getModels().map { it.id.id }
            println(models.joinToString())
            localDataStorage.setGptModels(models)
        }
    }

    private fun setModel(model: String) {
        viewModelScope.launch {
            localDataStorage.setCurrentGptModel(model)
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

    fun showAiSettings(show: Boolean) {
        _state.update { it.copy(showAiSettings = show) }
    }

    private fun setTemp(newTemp: Double) {
        logging().d { "newTemp: $newTemp" }
        val formattedTemp = "%.1f".format(newTemp)
        _state.update { it.copy(tempValue = formattedTemp.toDouble()) }
    }

    private fun applyChanges() {
        viewModelScope.launch {
            localDataStorage.setTemperature(state.value.tempValue)
            _state.update { it.copy(showAiSettings = false) }
        }
    }
}
