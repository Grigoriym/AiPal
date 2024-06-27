package com.grappim.aipal.android.feature.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            ),
        )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            localDataStorage.sstManager.collect { value ->
                _state.update { it.copy(currentSstManager = value) }
            }
        }
    }

    private fun onSstChange(currentSSTManager: CurrentSSTManager) {
        viewModelScope.launch {
            localDataStorage.setSstManager(currentSSTManager)
        }
    }
}
