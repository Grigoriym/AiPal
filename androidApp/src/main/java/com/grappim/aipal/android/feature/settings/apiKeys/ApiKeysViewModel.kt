package com.grappim.aipal.android.feature.settings.apiKeys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.core.LaunchedEffectResult
import com.grappim.aipal.android.data.local.LocalDataStorage
import com.grappim.aipal.android.data.repo.AiPalRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApiKeysViewModel(
    private val localDataStorage: LocalDataStorage,
    private val aiPalRepo: AiPalRepo
) : ViewModel() {
    private val _state = MutableStateFlow(
        ApiKeysState(
            onSetOpenAiApiKey = ::setOpenAiKey,
            saveOpenAiApiKey = ::saveOpenAiKey,
            onCheckApiKey = ::checkApiKey,
            onKeyClear = ::clearApiKey
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            localDataStorage.openAiApiKey.collect { value ->
                setOpenAiKey(value)
            }
        }
    }

    private fun clearApiKey() {
        setOpenAiKey("")
    }

    private fun checkApiKey() {
        viewModelScope.launch {
            aiPalRepo.getModels()
                .onSuccess {
                    _state.update { it.copy(apiKeyCheck = LaunchedEffectResult("Success")) }
                }
                .onFailure {
                    _state.update { it.copy(apiKeyCheck = LaunchedEffectResult("Failure")) }
                }
        }
    }

    private fun setOpenAiKey(key: String) {
        _state.update { it.copy(openAiApiKey = key) }
    }

    private fun saveOpenAiKey() {
        viewModelScope.launch {
            localDataStorage.setOpenAiApiKey(state.value.openAiApiKey)
        }
    }
}
