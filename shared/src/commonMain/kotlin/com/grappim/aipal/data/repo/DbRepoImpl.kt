package com.grappim.aipal.data.repo

import com.grappim.aipal.cache.Database
import com.grappim.aipal.cache.DatabaseDriverFactory
import com.grappim.aipal.data.db.model.Language
import com.grappim.aipal.data.db.model.Prompt
import com.grappim.aipal.data.db.model.PromptType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lighthousegames.logging.logging

class DbRepoImpl(databaseDriverFactory: DatabaseDriverFactory) : DbRepo {

    private val logging = logging()

    private val database = Database(databaseDriverFactory)

    override suspend fun checkAndPrepopulateDatabase(isDebug: Boolean) {
        database.checkAndPrepopulateDatabase()
        if (isDebug) {
            val langs = getAllLanguages()
            val prompts = getAllPrompts()
            logging.d {
                "checkAndPrepopulateDatabase: langs: ${langs.size}, prompts: ${prompts.size}"
            }
        }
    }

    override val languagesFlow: Flow<List<Language>> = database.languages

    override val promptsFlow: Flow<List<Prompt>> = database.prompts

    override val behaviorPromptFlow: Flow<Prompt> =
        promptsFlow.map { prompts: List<Prompt> ->
            prompts.first { it.promptType == PromptType.BEHAVIOR }
        }

    override val aiAnswerFixPromptFlow: Flow<Prompt> =
        promptsFlow.map { prompts: List<Prompt> ->
            prompts.first { it.promptType == PromptType.AI_FIX }
        }

    override suspend fun getPrompt(languageId: Long, type: PromptType): Prompt =
        database.getPromptByType(promptType = type, languageId = languageId)

    override suspend fun getAllLanguages(): List<Language> = database.getAllLanguages()

    override suspend fun getAllPrompts(): List<Prompt> = database.getAllPrompts()

    override suspend fun getPromptsByLanguageId(languageId: Long): List<Prompt> =
        database.getPromptsByLanguageId(languageId)

    override suspend fun savePrompt(content: String, languageId: Long, promptType: PromptType) {
        database.updatePrompt(
            content = content,
            languageId = languageId,
            type = promptType.title
        )
    }
}
