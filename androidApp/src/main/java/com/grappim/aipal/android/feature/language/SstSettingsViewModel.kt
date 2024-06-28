package com.grappim.aipal.android.feature.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.CurrentSSTManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SstSettingsViewModel(
    private val localDataStorage: LocalDataStorage,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SstSettingsState(
                currentSstManager = CurrentSSTManager.default(),
                onSstManagerChange = ::onSstChange,
                description = CurrentSSTManager.default().description,
                currentLanguage = SupportedLanguage.getDefault(),
                onSetCurrentLanguage = ::setLanguage,
                languages = SupportedLanguage.entries.map { it.name }.toSet()
            ),
        )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.sstManager.collect { value ->
                    _state.update { it.copy(currentSstManager = value) }
                }
            }
            launch {
                localDataStorage.currentLanguage.collect { lng ->
                    _state.update { it.copy(currentLanguage = lng) }
                }
            }
        }
    }

    private fun onSstChange(currentSSTManager: CurrentSSTManager) {
        viewModelScope.launch {
            localDataStorage.setSstManager(currentSSTManager)
        }
    }

    private fun setLanguage(supportedLanguage: String) {
        viewModelScope.launch {
            val enum = SupportedLanguage.valueOf(supportedLanguage)
            localDataStorage.setCurrentLanguage(enum)
        }
    }
}
