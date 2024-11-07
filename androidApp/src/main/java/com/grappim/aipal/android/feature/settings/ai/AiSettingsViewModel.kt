package com.grappim.aipal.android.feature.settings.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.feature.settings.ai.AiSettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import java.util.Locale

class AiSettingsViewModel(
    private val localDataStorage: LocalDataStorage,
    private val aiPalRepo: AiPalRepo,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            AiSettingsState(
                onSetTempValue = ::setTemp,
                applyTemp = ::applyTemp,
                onGetModels = ::getModels,
                onSetModel = ::setModel,
            ),
        )
    val state = _state.asStateFlow()

    private val logging = logging()

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
        logging.d { "newTemp: $newTemp" }
        val formattedTemp = "%.1f".format(Locale.US, newTemp)
        _state.update { it.copy(tempValue = formattedTemp.toDouble()) }
    }

    private fun applyTemp() {
        viewModelScope.launch {
            localDataStorage.setTemperature(state.value.tempValue)
        }
    }

    private fun getModels() {
        viewModelScope.launch {
            aiPalRepo.getModels()
                .onSuccess { models ->
                    _state.update {
                        it.copy(
                            snackbarMessage = LaunchedEffectResult(
                                "Models were downloaded"
                            )
                        )
                    }
                    localDataStorage.setGptModels(models)
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            snackbarMessage = LaunchedEffectResult(
                                exception.message ?: ""
                            )
                        )
                    }
                }
        }
    }

    private fun setModel(model: String) {
        viewModelScope.launch {
            localDataStorage.setCurrentGptModel(model)
        }
    }
}
