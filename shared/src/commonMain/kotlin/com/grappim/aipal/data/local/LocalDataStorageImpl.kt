package com.grappim.aipal.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.grappim.aipal.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.core.DEFAULT_MODEL
import com.grappim.aipal.core.DEFAULT_TEMPERATURE
import com.grappim.aipal.core.DEFAULT_TRANSLATION_PROMPT
import com.grappim.aipal.core.SupportedLanguage
import com.grappim.aipal.data.model.DarkThemeConfig
import com.grappim.aipal.data.recognition.CurrentSSTManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataStorageImpl(
    private val dataStore: DataStore<Preferences>,
) : LocalDataStorage {
    /**
     * https://platform.openai.com/docs/api-reference/chat/create#chat-create-temperature
     */
    private val tempKey = doublePreferencesKey("temp_key")
    override val tempFlow: Flow<Double> =
        dataStore.data
            .map { value: Preferences ->
                value[tempKey] ?: DEFAULT_TEMPERATURE
            }

    private val sstManagerKey = stringPreferencesKey("sst_manager_key")
    override val sstManager: Flow<CurrentSSTManager> =
        dataStore.data
            .map { value: Preferences ->
                CurrentSSTManager.valueOf(value[sstManagerKey] ?: CurrentSSTManager.default().name)
            }

    private val currentLanguageKey = stringPreferencesKey("language_key")
    override val currentLanguage: Flow<SupportedLanguage> =
        dataStore.data.map { value: Preferences ->
            SupportedLanguage.valueOf(
                value[currentLanguageKey] ?: SupportedLanguage.getDefault().name,
            )
        }

    private val openAiApiKeyKey = stringPreferencesKey("open_ai_api_key_key")
    override val openAiApiKey: Flow<String> =
        dataStore.data
            .map { value ->
                value[openAiApiKeyKey] ?: ""
            }

    private val translationPromptKey = stringPreferencesKey("translation_prompt_key")
    override val translationPrompt: Flow<String> =
        dataStore.data.map { value ->
            value[translationPromptKey] ?: DEFAULT_TRANSLATION_PROMPT
        }

    private val behaviorKey = stringPreferencesKey("behavior_key")
    override val behavior: Flow<String> =
        dataStore.data.map { value ->
            value[behaviorKey] ?: DEFAULT_BEHAVIOR
        }

    private val currentGptModelKey = stringPreferencesKey("current_gpt_key")
    override val currentGptModel: Flow<String> =
        dataStore.data
            .map { value: Preferences ->
                value[currentGptModelKey] ?: DEFAULT_MODEL
            }

    private val gptModelsKey = stringSetPreferencesKey("gpt_models_key")
    override val gptModels: Flow<Set<String>> =
        dataStore.data.map { value: Preferences ->
            value[gptModelsKey] ?: emptySet()
        }

    private val darkThemeKey = stringPreferencesKey("dark_theme_key")
    override val darkThemeConfig: Flow<DarkThemeConfig> =
        dataStore.data
            .map { preferences ->
                DarkThemeConfig.fromValue(preferences[darkThemeKey]) ?: DarkThemeConfig.default()
            }

    override suspend fun setGptModels(models: List<String>) {
        dataStore.edit { settings ->
            settings[gptModelsKey] = models.toSet()
        }
    }

    override suspend fun setCurrentGptModel(model: String) {
        dataStore.edit { settings ->
            settings[currentGptModelKey] = model
        }
    }

    override suspend fun setTemperature(temp: Double) {
        dataStore.edit { settings ->
            settings[tempKey] = temp
        }
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        dataStore.edit { settings ->
            settings[darkThemeKey] = darkThemeConfig.value
        }
    }

    override suspend fun setTranslationPrompt(prompt: String) {
        dataStore.edit { settings ->
            settings[translationPromptKey] = prompt
        }
    }

    override suspend fun setBehavior(text: String) {
        dataStore.edit { settings ->
            settings[behaviorKey] = text
        }
    }

    override suspend fun setOpenAiApiKey(key: String) {
        dataStore.edit { settings ->
            settings[openAiApiKeyKey] = key
        }
    }

    override suspend fun setCurrentLanguage(language: SupportedLanguage) {
        dataStore.edit { settings ->
            settings[currentLanguageKey] = language.name
        }
    }

    override suspend fun setSstManager(sstManager: CurrentSSTManager) {
        dataStore.edit { settings ->
            settings[sstManagerKey] = sstManager.name
        }
    }
}
