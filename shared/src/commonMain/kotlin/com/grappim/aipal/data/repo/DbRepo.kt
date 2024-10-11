package com.grappim.aipal.data.repo

import com.grappim.aipal.data.db.model.Language
import com.grappim.aipal.data.db.model.Prompt
import com.grappim.aipal.data.db.model.PromptType
import kotlinx.coroutines.flow.Flow

interface DbRepo {
    val languagesFlow: Flow<List<Language>>
    val promptsFlow: Flow<List<Prompt>>

    val behaviorPromptFlow: Flow<Prompt>
    val aiAnswerFixPromptFlow: Flow<Prompt>

    suspend fun getPrompt(languageId: Long, type: PromptType): Prompt

    suspend fun checkAndPrepopulateDatabase(isDebug: Boolean)

    suspend fun getAllLanguages(): List<Language>
    suspend fun getAllPrompts(): List<Prompt>
    suspend fun getPromptsByLanguageId(languageId: Long): List<Prompt>

    suspend fun savePrompt(content: String, languageId: Long, promptType: PromptType)
}
