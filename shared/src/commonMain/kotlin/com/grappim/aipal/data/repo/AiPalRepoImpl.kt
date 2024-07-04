package com.grappim.aipal.data.repo

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.RetryStrategy
import com.grappim.aipal.data.exceptions.OpenAiEmptyApiKeyException
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.model.Message
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.seconds

class AiPalRepoImpl(
    private val localDataStorage: LocalDataStorage,
) : AiPalRepo {
    private val messages = mutableListOf<Message>()

    private val logging = logging()

    private val openAiFlow: Flow<OpenAI?> =
        localDataStorage.openAiApiKey
            .map { value ->
                if (value.isEmpty()) return@map null
                OpenAI(
                    retry = RetryStrategy(
                        maxRetries = 0,
                        maxDelay = 30.seconds
                    ),
                    token = value,
                    logging =
                    LoggingConfig(
                        logLevel = LogLevel.All,
                    ),
                )
            }

    private suspend fun getOpenAi(): OpenAI? = openAiFlow.firstOrNull()

    override suspend fun checkSpelling(msg: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
            val chatCompletionRequest =
                createChatCompletionRequest(
                    listOf(
                        ChatMessage(
                            role = Role.System,
                            content = localDataStorage.spellingPrompt.first(),
                        ),
                        ChatMessage(
                            role = Role.User,
                            content = msg,
                        ),
                    ),
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
            logging.e { e }
            Result.failure(e)
        }
    }

    override suspend fun translateMessage(msg: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                val chatCompletionRequest =
                    createChatCompletionRequest(
                        listOf(
                            ChatMessage(
                                role = Role.System,
                                content = localDataStorage.translationPrompt.first(),
                            ),
                            ChatMessage(
                                role = Role.User,
                                content = "\"$msg\"",
                            ),
                        ),
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
                logging.e { e }
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

    override suspend fun getModels(): Result<List<String>> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                Result.success(service.models().map { it.id.id })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun sendMessage(msg: String): Result<String> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                messages.add(Message(msg, Role.User))
                val chatCompletionRequest =
                    createChatCompletionRequest(
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
                logging.d { result }
                return@withContext Result.success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }
        }

    private suspend fun createChatCompletionRequest(messages: List<ChatMessage>): ChatCompletionRequest =
        ChatCompletionRequest(
            temperature = localDataStorage.tempFlow.first(),
            model = ModelId(localDataStorage.currentGptModel.first()),
            messages = messages,
        )
}
