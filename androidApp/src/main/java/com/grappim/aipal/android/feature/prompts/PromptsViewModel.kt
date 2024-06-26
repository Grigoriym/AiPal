package com.grappim.aipal.android.feature.prompts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.feature.prompts.PromptsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptsViewModel(
    private val localDataStorage: LocalDataStorage,
    private val aiPalRepo: AiPalRepo
) : ViewModel() {
    private val _state = MutableStateFlow(
        PromptsState(
            onSetTranslationPrompt = ::setTranslationPrompt,
            onSetBehavior = ::setBehavior,
            saveBehavior = ::saveBehavior,
            saveTranslationPrompt = ::saveTranslationPrompt
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.translationPrompt.collect { value ->
                    setTranslationPrompt(value)
                }
            }
            launch {
                localDataStorage.behavior.collect { value ->
                    setBehavior(value)
                    aiPalRepo.setBehavior(state.value.behavior)
                }
            }
        }
    }

    private fun setBehavior(text: String) {
        _state.update { it.copy(behavior = text) }
    }

    private fun setTranslationPrompt(text: String) {
        _state.update { it.copy(translationPrompt = text) }
    }

    private fun saveBehavior() {
        viewModelScope.launch {
            localDataStorage.setBehavior(state.value.behavior)
        }
    }

    private fun saveTranslationPrompt() {
        viewModelScope.launch {
            localDataStorage.setTranslationPrompt(state.value.translationPrompt)
        }
    }
}
