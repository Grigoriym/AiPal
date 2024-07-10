package com.grappim.aipal.data.repo

import com.aallam.openai.api.chat.ChatCompletion
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
import com.grappim.aipal.data.model.MessageType
import com.grappim.aipal.data.model.ResultMessage
import com.grappim.aipal.data.uuid.UuidGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging
import kotlin.time.Duration.Companion.seconds

class AiPalRepoImpl(
    private val localDataStorage: LocalDataStorage,
    private val uuidGenerator: UuidGenerator
) : AiPalRepo {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private val messages: ThreadSafeMessageList = ThreadSafeMessageList()

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

    init {
        scope.launch {
            launch {
                localDataStorage.currentLanguage.collect { value ->
                    updateOrAddSystemMessage(
                        messageType = MessageType.LANGUAGE,
                        newText = "Let's talk in ${value.title}"
                    )
                }
            }
            launch {
                localDataStorage.behavior.collect { value ->
                    updateOrAddSystemMessage(
                        messageType = MessageType.BEHAVIOR,
                        newText = value
                    )
                }
            }
            launch {
                localDataStorage.aiAnswerFixPrompt.collect { value ->
                    updateOrAddSystemMessage(
                        messageType = MessageType.AI_FIX,
                        newText = value
                    )
                }
            }
        }
    }

    private suspend fun updateOrAddSystemMessage(messageType: MessageType, newText: String) {
        val currentMessages = messages.getMessages()
        val existingMessage = currentMessages.find {
            it.role == Role.System && it.messageType == messageType
        }

        if (existingMessage == null) {
            val newId = uuidGenerator.getUuid4()
            messages.addMessage(
                Message(
                    id = newId,
                    text = newText,
                    role = Role.System,
                    messageType = messageType
                )
            )
        } else {
            messages.removeMessageById(existingMessage.id)
            messages.addMessage(existingMessage.copy(text = newText))
        }
    }

    private suspend fun getOpenAi(): OpenAI =
        openAiFlow.firstOrNull() ?: throw OpenAiEmptyApiKeyException()

    override suspend fun checkSpelling(msg: String): Result<String> =
        withContext(Dispatchers.Default) {
            try {
                val service = getOpenAi()
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
        withContext(Dispatchers.Default) {
            try {
                val service = getOpenAi()
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

    override suspend fun getModels(): Result<List<String>> =
        withContext(Dispatchers.Default) {
            try {
                val service = getOpenAi()
                Result.success(service.models().map { it.id.id })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logging.e { e }
                Result.failure(e)
            }
        }

    override suspend fun sendMessage(msg: String, msgId: String): Result<ResultMessage> =
        withContext(Dispatchers.Default) {
            try {
                val service = getOpenAi()
                messages.addMessage(Message(msgId, msg, Role.User))
                val chatCompletionRequest =
                    createChatCompletionRequest(
                        messages.getMessages().map {
                            ChatMessage(
                                role = it.role,
                                content = it.text,
                            )
                        },
                    )
                val completion = service.chatCompletion(chatCompletionRequest)
                val result = processCompletion(completion)
                val resultMessageId = uuidGenerator.getUuid4()
                messages.addMessage(
                    Message(
                        id = resultMessageId,
                        text = result,
                        role = Role.Assistant
                    )
                )
                val resultMessage = ResultMessage(
                    id = resultMessageId,
                    text = result
                )
                logging.d { resultMessage }
                Result.success(resultMessage)
            } catch (e: CancellationException) {
                removeMessageById(msgId)
                throw e
            } catch (e: Exception) {
                logging.e { e }
                removeMessageById(msgId)
                Result.failure(e)
            }
        }

    private fun processCompletion(completion: ChatCompletion): String {
        return completion.choices.firstOrNull()?.message?.content
            ?: error("Empty response message content")
    }

    private suspend fun removeMessageById(id: String) {
        messages.removeMessageById(id)
    }

    private suspend fun createChatCompletionRequest(messages: List<ChatMessage>): ChatCompletionRequest =
        ChatCompletionRequest(
            temperature = localDataStorage.tempFlow.first(),
            model = ModelId(localDataStorage.currentGptModel.first()),
            messages = messages,
        )
}
