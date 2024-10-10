package com.grappim.aipal.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.grappim.aipal.AipalDb
import com.grappim.aipal.data.db.model.Language
import com.grappim.aipal.data.db.model.Prompt
import com.grappim.aipal.data.db.model.PromptType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext

internal class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AipalDb(databaseDriverFactory.createDriver())
    private val dbQuery = database.aipalDbQueries

    suspend fun checkAndPrepopulateDatabase() = withContext(Dispatchers.Default) {
        val count = dbQuery.getAllLanguages().executeAsList().size
        if (count == 0) {
            dbQuery.insertInitialLanguages()
            dbQuery.insertInitialPrompts()
        }
    }

    private fun mapLanguage(id: Long, name: String, lngCode: String) = Language(
        id = id,
        name = name,
        lngCode = lngCode
    )

    private fun mapPrompts(id: Long, content: String, languageId: Long, type: String) = Prompt(
        id = id,
        content = content,
        languageId = languageId,
        promptType = PromptType.getTypeFromString(type)
    )

    val languages: Flow<List<Language>> = dbQuery.getAllLanguages(::mapLanguage)
        .asFlow()
        .mapToList(Dispatchers.Default)
        .filter { it.isNotEmpty() }

    val prompts: Flow<List<Prompt>> = dbQuery.getAllPrompts(::mapPrompts)
        .asFlow()
        .mapToList(Dispatchers.Default)
        .filter { it.isNotEmpty() }

    fun getPromptByType(promptType: PromptType, languageId: Long): Prompt {
        return dbQuery.getPromptBYLanguageAndType(
            languageId = languageId,
            type = promptType.title,
            ::mapPrompts
        ).executeAsOne()
    }

    internal suspend fun getAllLanguages(): List<Language> = withContext(Dispatchers.Default) {
        dbQuery.getAllLanguages(::mapLanguage).executeAsList()
    }

    internal suspend fun getAllPrompts(): List<Prompt> = withContext(Dispatchers.Default) {
        dbQuery.getAllPrompts(::mapPrompts).executeAsList()
    }

    internal suspend fun getPromptsByLanguageId(languageId: Long): List<Prompt> =
        withContext(Dispatchers.Default) {
            dbQuery.getPromptsByLanguageId(languageId, ::mapPrompts).executeAsList()
        }

    internal suspend fun updatePrompt(
        content: String,
        languageId: Long,
        type: String
    ) = withContext(Dispatchers.Default) {
        dbQuery.updatePromptByType(
            content = content,
            languageId = languageId,
            type = type,
        )
    }
}
