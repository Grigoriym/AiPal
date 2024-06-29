package com.grappim.aipal.android.feature.stt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.CurrentSTTManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SttSettingsViewModel(
    private val localDataStorage: LocalDataStorage,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SttSettingsState(
                currentSTTManager = CurrentSTTManager.default(),
                onSstManagerChange = ::onSstChange,
                description = CurrentSTTManager.default().description,
                currentLanguage = SupportedLanguage.getDefault(),
                onSetCurrentLanguage = ::setLanguage,
                languages = SupportedLanguage.entries.map { it.name }.toSet()
            ),
        )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.sttManager.collect { value ->
                    _state.update { it.copy(currentSTTManager = value) }
                }
            }
            launch {
                localDataStorage.currentLanguage.collect { lng ->
                    _state.update { it.copy(currentLanguage = lng) }
                }
            }
        }
    }

    private fun onSstChange(currentSTTManager: CurrentSTTManager) {
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
