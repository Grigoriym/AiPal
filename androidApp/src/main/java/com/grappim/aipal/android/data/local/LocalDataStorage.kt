package com.grappim.aipal.android.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.grappim.aipal.android.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.android.core.DEFAULT_MODEL
import com.grappim.aipal.android.core.DEFAULT_TEMPERATURE
import com.grappim.aipal.android.core.DEFAULT_TRANSLATION_PROMPT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LocalDataStorage {
    val tempFlow: Flow<Double>
    val currentGptModel: Flow<String>
    val gptModels: Flow<Set<String>>
    val darkThemeConfig: Flow<DarkThemeConfig>
    val translationPrompt: Flow<String>
    val behavior: Flow<String>

    suspend fun setCurrentGptModel(model: String)

    suspend fun setTemperature(temp: Double)

    suspend fun setGptModels(models: List<String>)
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    suspend fun setTranslationPrompt(prompt: String)
    suspend fun setBehavior(text: String)
}

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

    private val translationPromptKey = stringPreferencesKey("translation_prompt_key")
    override val translationPrompt: Flow<String> = dataStore.data.map { value ->
        value[translationPromptKey] ?: DEFAULT_TRANSLATION_PROMPT
    }

    private val behaviorKey = stringPreferencesKey("behavior_key")
    override val behavior: Flow<String> = dataStore.data.map { value ->
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
    override val darkThemeConfig: Flow<DarkThemeConfig> = dataStore.data
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
}
