package com.grappim.aipal.android.feature.stt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.files.vosk.VoskModelAvailability
import com.grappim.aipal.android.recognition.Downloadable
import com.grappim.aipal.android.recognition.ModelAvailabilityRetrieval
import com.grappim.aipal.android.recognition.factory.STTFactory
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTManager
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
                onModelDownload = ::downloadModel
            ),
        )
    val state = _state.asStateFlow()

    init {
        sttManager = runBlocking { sttFactory.getSSTManager(localDataStorage.sttManager.first()) }
        viewModelScope.launch {
            launch {
                localDataStorage.sttManager.distinctUntilChanged().collect { newStt ->
                    _state.update { it.copy(currentSTTManager = newStt) }

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
        viewModelScope.launch {
            sttManager.cancel()
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
                        it.copy(availableModels = oldList.toList())
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

    override fun onCleared() {
        super.onCleared()
        sttManager.cancel()
    }
}

inline fun <reified T> Any.runAs(block: T.() -> Unit) {
    if (this is T) {
        block()
    }
}
