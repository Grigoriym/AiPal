package com.grappim.aipal.android.feature.stt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.android.recognition.Downloadable
import com.grappim.aipal.android.recognition.ModelAvailabilityRetrieval
import com.grappim.aipal.core.LaunchedEffectResult
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTManager
import com.grappim.aipal.data.recognition.STTFactory
import com.grappim.aipal.feature.chat.SnackbarData
import com.grappim.aipal.utils.runAs
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.lighthousegames.logging.logging

class SttSettingsViewModel(
    private val localDataStorage: LocalDataStorage,
    private val sttFactory: STTFactory,
) : ViewModel() {
    private var sttManager: STTManager

    private var runningJob: Job? = null

    private val logging = logging()

    private val _state =
        MutableStateFlow(
            SttSettingsState(
                currentSTTManager = CurrentSTTManager.default(),
                onSttManagerChange = ::onSttChange,
                description = CurrentSTTManager.default().description,
                currentLanguage = SupportedLanguage.getDefault(),
                onSetCurrentLanguage = ::setLanguage,
                languages = SupportedLanguage.entries.map { it.name }.toSet(),
                onModelDownload = ::downloadModel,
                onDismissDialog = ::dismissDialog,
                acknowledgeError = ::acknowledgeError
            ),
        )
    val state = _state.asStateFlow()

    init {
        sttManager = runBlocking { sttFactory.getSSTManager(localDataStorage.sttManager.first()) }
        viewModelScope.launch {
            launch {
                localDataStorage.sttManager.distinctUntilChanged().collect { newStt ->
                    _state.update { it.copy(currentSTTManager = newStt) }

                    logging.d { "received new stt: $newStt" }
                    updateSttManager(newStt)
                }
            }
            launch {
                localDataStorage.currentLanguage.distinctUntilChanged().collect { lng ->
                    _state.update { it.copy(currentLanguage = lng) }
                    sttManager.changeLanguage(lng)
                }
            }
        }
    }

    private fun acknowledgeError() {
        sttManager.resetToDefaultState()
    }

    private fun downloadModel(voskModelAvailability: VoskModelAvailability) {
        viewModelScope.launch {
            _state.update {
                it.copy(isDownloading = true)
            }
            if (sttManager is Downloadable) {
                sttManager.runAs<Downloadable> {
                    downloadModelFile(voskModelAvailability.supportedLanguage)
                    checkAvailableModels()
                }
            }
            _state.update {
                it.copy(isDownloading = false)
            }
        }
    }

    private fun dismissDialog() {
        _state.update { it.copy(showAlertDialog = true) }
    }

    private suspend fun checkAvailableModels() {
        if (sttManager is ModelAvailabilityRetrieval) {
            sttManager.runAs<ModelAvailabilityRetrieval> {
                val ui = whichModelsAvailable().map { model ->
                    VoskModelUI(voskModelAvailability = model)
                }
                _state.update {
                    it.copy(availableModels = ui)
                }
            }
        }
    }

    private fun updateSttManager(newManger: CurrentSTTManager) {
        runningJob?.cancel()
        runningJob = viewModelScope.launch {
            sttManager = sttFactory.getSSTManager(newManger)

            checkAvailableModels()

            sttManager.state.collect { value ->
                if (sttManager is Downloadable) {
                    logging.d { "state: $value" }
                    val result = value.modelRetrievalResult
                    val modelToUpdate = requireNotNull(state.value.availableModels.find {
                        it.voskModelAvailability.supportedLanguage == result.supportedLanguage
                    })
                    val oldList = state.value.availableModels.toMutableList()
                    val index = oldList.indexOf(modelToUpdate)

                    oldList[index] = modelToUpdate.copy(
                        voskModelUIState = result.modelRetrievalState
                    )
                    _state.update {
                        it.copy(
                            availableModels = oldList.toList(),
                            showAlertDialog = value.modelRetrievalResult.showAlertDialog(),
                            snackbarMessage = LaunchedEffectResult(SnackbarData(message = value.error)),
                        )
                    }
                }
            }
        }
    }

    private fun onSttChange(currentSTTManager: CurrentSTTManager) {
        viewModelScope.launch {
            localDataStorage.setSttManager(currentSTTManager)
        }
    }

    private fun setLanguage(supportedLanguage: String) {
        viewModelScope.launch {
            val enum = SupportedLanguage.valueOf(supportedLanguage)
            localDataStorage.setCurrentLanguage(enum)
        }
    }
}
