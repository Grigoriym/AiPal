package com.grappim.aipal.feature.prompts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grappim.aipal.data.db.model.Prompt
import com.grappim.aipal.data.db.model.PromptType
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.DbRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PromptsViewModel(
    private val localDataStorage: LocalDataStorage,
    private val dbRepo: DbRepo
) : ViewModel() {
    private val _state = MutableStateFlow(
        PromptsState(
            onUpdatePromptElement = ::updatePromptElement,
        )
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
                localDataStorage.currentLanguage.collect { value ->
                    val prompts = dbRepo.getPromptsByLanguageId(value.dbId)

                    val elements: List<PromptStateElement> = prompts.mapNotNull { prompt: Prompt ->
                        when (prompt.promptType) {
                            PromptType.BEHAVIOR -> {
                                PromptStateElement(
                                    title = "Behavior sets the initial setup for the AI, i.e. context",
                                    value = prompt.content,
                                    label = "Behavior prompt",
                                    onSave = {
                                        savePrompt(prompt, it)
                                    },
                                    saveButtonText = "Save Behavior",
                                    languageId = prompt.languageId,
                                    type = prompt.promptType
                                )
                            }

                            PromptType.TRANSLATION -> {
                                PromptStateElement(
                                    title = "Translation Prompt sets the message which is sent upon translating the text",
                                    value = prompt.content,
                                    label = "Translation prompt",
                                    onSave = {
                                        savePrompt(prompt, it)
                                    },
                                    saveButtonText = "Save Translation Prompt",
                                    languageId = prompt.languageId,
                                    type = prompt.promptType
                                )
                            }

                            PromptType.SPELLING -> {
                                PromptStateElement(
                                    title = "Spelling Check Prompt sets the message which is sent upon checking the spelling",
                                    value = prompt.content,
                                    label = "Spelling check prompt",
                                    onSave = {
                                        savePrompt(prompt, it)
                                    },
                                    saveButtonText = "Save Spelling check prompt",
                                    languageId = prompt.languageId,
                                    type = prompt.promptType
                                )
                            }

                            PromptType.AI_FIX -> {
                                PromptStateElement(
                                    title = "A Prompt for removing some symbols from AI answer so that TTS engine wouldn't pronounce them",
                                    value = prompt.content,
                                    label = "Ai fix Prompt",
                                    onSave = {
                                        savePrompt(prompt, it)
                                    },
                                    saveButtonText = "Save Ai fix prompt",
                                    languageId = prompt.languageId,
                                    type = prompt.promptType
                                )
                            }

                            else -> {
                                null
                            }
                        }
                    }

                    _state.update {
                        it.copy(promptElements = elements)
                    }
                }
            }
        }
    }

    //TODO need to do something with this
    private fun updatePromptElement(index: Int, newValue: String) {
        val updatedList = _state.value.promptElements.toMutableList().apply {
            this[index] = this[index].copy(value = newValue)
        }
        _state.value = _state.value.copy(promptElements = updatedList)
    }

    private fun savePrompt(prompt: Prompt, index: Int) {
        viewModelScope.launch {
            val content = state.value.promptElements[index].value

            dbRepo.savePrompt(
                content = content,
                languageId = prompt.languageId,
                promptType = prompt.promptType
            )
        }
    }
}
