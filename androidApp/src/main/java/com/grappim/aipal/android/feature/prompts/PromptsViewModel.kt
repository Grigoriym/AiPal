package com.grappim.aipal.android.feature.prompts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.feature.prompts.PromptsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptsViewModel(
    private val localDataStorage: LocalDataStorage
) : ViewModel() {
    private val _state = MutableStateFlow(
        PromptsState(
            onSetTranslationPrompt = ::setTranslationPrompt,
            saveTranslationPrompt = ::saveTranslationPrompt,
            onSetBehavior = ::setBehavior,
            saveBehavior = ::saveBehavior,
            onSetSpelling = ::setSpelling,
            saveSpelling = ::saveSpellingPrompt
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
                }
            }
            launch {
                localDataStorage.spellingPrompt.collect { value ->
                    setSpelling(value)
                }
            }
        }
    }

    private fun setSpelling(text: String) {
        _state.update { it.copy(spellingCheckPrompt = text) }
    }

    private fun saveSpellingPrompt() {
        viewModelScope.launch {
            localDataStorage.setSpellingPrompt(state.value.spellingCheckPrompt)
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
