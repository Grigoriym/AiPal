package com.grappim.aipal.android.feature.settings.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.data.local.LocalDataStorage
import com.grappim.aipal.android.data.repo.AiPalRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class AiSettingsViewModel(
    private val localDataStorage: LocalDataStorage,
    private val aiPalRepo: AiPalRepo,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            AiSettingsState(
                onSetTempValue = ::setTemp,
                applySettings = ::applyChanges,
                onGetModels = ::getModels,
                onSetModel = ::setModel,
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
                localDataStorage.gptModels.collect { value ->
                    _state.update { it.copy(models = value) }
                }
            }
            launch {
                localDataStorage.currentGptModel.collect { value ->
                    _state.update { it.copy(selectedModel = value) }
                }
            }
        }
    }

    private fun setTemp(newTemp: Double) {
        logging().d { "newTemp: $newTemp" }
        val formattedTemp = "%.1f".format(newTemp)
        _state.update { it.copy(tempValue = formattedTemp.toDouble()) }
    }

    private fun applyChanges() {
        viewModelScope.launch {
            localDataStorage.setTemperature(state.value.tempValue)
        }
    }

    private fun getModels() {
        viewModelScope.launch {
            val models = aiPalRepo.getModels().getOrDefault(emptyList()).map { it.id.id }
            println(models.joinToString())
            localDataStorage.setGptModels(models)
        }
    }

    private fun setModel(model: String) {
        viewModelScope.launch {
            localDataStorage.setCurrentGptModel(model)
        }
    }
}
