package com.grappim.aipal.data

import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.model.DarkThemeConfig
import com.grappim.aipal.data.recognition.CurrentSTTManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DesktopLocalDataStorage : LocalDataStorage {
    override val tempFlow: Flow<Double>
        get() = flow { }
    override val currentGptModel: Flow<String>
        get() = flow { }
    override val gptModels: Flow<Set<String>>
        get() = flow { }
    override val darkThemeConfig: Flow<DarkThemeConfig>
        get() = flow { }
    override val translationPrompt: Flow<String>
        get() = flow { }
    override val behavior: Flow<String>
        get() = flow { }
    override val openAiApiKey: Flow<String>
        get() = flow { }
    override val currentLanguage: Flow<SupportedLanguage>
        get() = flow { }
    override val sttManager: Flow<CurrentSTTManager>
        get() = flow { }
    override val spellingPrompt: Flow<String>
        get() = flow { }
    override val aiAnswerFixPrompt: Flow<String>
        get() = flow { }

    override suspend fun setCurrentGptModel(model: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setTemperature(temp: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun setGptModels(models: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        TODO("Not yet implemented")
    }

    override suspend fun setTranslationPrompt(prompt: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setBehavior(text: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setOpenAiApiKey(key: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setCurrentLanguage(language: SupportedLanguage) {
        TODO("Not yet implemented")
    }

    override suspend fun setSttManager(sstManager: CurrentSTTManager) {
        TODO("Not yet implemented")
    }

    override suspend fun setSpellingPrompt(prompt: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setAiAnswerFixPrompt(prompt: String) {
        TODO("Not yet implemented")
    }
}
