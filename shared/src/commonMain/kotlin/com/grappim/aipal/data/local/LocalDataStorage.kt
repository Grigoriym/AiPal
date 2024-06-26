package com.grappim.aipal.data.local

import com.grappim.aipal.data.model.DarkThemeConfig
import kotlinx.coroutines.flow.Flow

interface LocalDataStorage {
    val tempFlow: Flow<Double>
    val currentGptModel: Flow<String>
    val gptModels: Flow<Set<String>>
    val darkThemeConfig: Flow<DarkThemeConfig>
    val translationPrompt: Flow<String>
    val behavior: Flow<String>
    val openAiApiKey: Flow<String>

    suspend fun setCurrentGptModel(model: String)

    suspend fun setTemperature(temp: Double)

    suspend fun setGptModels(models: List<String>)
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setTranslationPrompt(prompt: String)
    suspend fun setBehavior(text: String)
    suspend fun setOpenAiApiKey(key: String)
}
