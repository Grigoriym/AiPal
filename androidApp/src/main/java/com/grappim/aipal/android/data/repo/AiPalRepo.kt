package com.grappim.aipal.android.data.repo

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.Model
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.grappim.aipal.android.core.DEFAULT_BEHAVIOR
import com.grappim.aipal.android.data.OpenAiEmptyApiKeyException
import com.grappim.aipal.android.data.local.LocalDataStorage
import com.grappim.aipal.android.data.model.Message
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

interface AiPalRepo {
    suspend fun getModels(): Result<List<Model>>

    suspend fun sendMessage(msg: String): Result<String>

    fun setBehavior(msg: String)

    suspend fun translateMessage(msg: String): Result<String>
}

class AiPalRepoImpl(
    private val localDataStorage: LocalDataStorage,
) : AiPalRepo {

    private val messages = mutableListOf<Message>()

    private val log = logging()

    private val openAiFlow: Flow<OpenAI?> =
        localDataStorage.openAiApiKey
            .map { value ->
                if (value.isEmpty()) return@map null
                OpenAI(
                    token = value,
                    logging =
                    LoggingConfig(
                        logLevel = LogLevel.All,
                    ),
                )
            }

    init {
        setBehavior(DEFAULT_BEHAVIOR)
    }

    private suspend fun getOpenAi(): OpenAI? = openAiFlow.firstOrNull()

    override suspend fun translateMessage(msg: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                val chatCompletionRequest = createChatCompletionRequest(
                    listOf(
                        ChatMessage(
                            role = Role.System,
                            content = localDataStorage.translationPrompt.first(),
                        ),
                        ChatMessage(
                            role = Role.User,
                            content = "\"$msg\"",
                        ),
                    )
                )
                val completion = service.chatCompletion(chatCompletionRequest)
                val receivedMessage =
                    completion.choices
                        .first()
                        .message
                Result.success(receivedMessage.content ?: "")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override fun setBehavior(msg: String) {
        val presentBehavior = messages.find { it.role == Role.System }
        if (presentBehavior == null) {
            messages.add(Message(msg, Role.System))
        } else {
            val newBehavior = presentBehavior.copy(text = msg)
            messages[messages.indexOf(presentBehavior)] = newBehavior
        }
    }

    override suspend fun getModels(): Result<List<Model>> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                Result.success(service.models())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun sendMessage(msg: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                messages.add(Message(msg, Role.User))
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                val chatCompletionRequest = createChatCompletionRequest(
                    messages.map {
                        ChatMessage(
                            role = it.role,
                            content = it.text,
                        )
                    },
                )
                val completion = service.chatCompletion(chatCompletionRequest)
                val receivedMessage =
                    completion.choices
                        .first()
                        .message
                val result = receivedMessage.content ?: ""
                messages.add(Message(result, Role.Assistant))
                log.d { result }
                return@withContext Result.success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    private suspend fun createChatCompletionRequest(
        messages: List<ChatMessage>
    ): ChatCompletionRequest =
        ChatCompletionRequest(
            temperature = localDataStorage.tempFlow.first(),
            model = ModelId(localDataStorage.currentGptModel.first()),
            messages = messages
        )
}
