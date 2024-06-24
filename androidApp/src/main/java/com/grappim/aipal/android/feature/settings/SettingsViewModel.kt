package com.grappim.aipal.android.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.android.data.repo.AiPalRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val aiPalRepo: AiPalRepo
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    fun getModels() {
        viewModelScope.launch {
            val models = aiPalRepo.getModels().map { it.id.id }
            println(models.joinToString())
            _state.update { it.copy(models = models) }
        }
    }

    fun setModel(model: String) {
        viewModelScope.launch {
            _state.update { it.copy(selectedModel = model) }
            aiPalRepo.setModel(model)
        }
    }

    fun updateBehavior(text: String) {
        _state.update { it.copy(behavior = text) }
    }

    fun setBehavior() {
        viewModelScope.launch {
            aiPalRepo.setBehavior(state.value.behavior)
        }
    }
}
