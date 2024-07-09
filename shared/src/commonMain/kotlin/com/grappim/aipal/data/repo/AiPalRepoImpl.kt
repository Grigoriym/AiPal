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
import com.grappim.aipal.data.model.MessageType
import com.grappim.aipal.data.model.ResultMessage
import com.grappim.aipal.data.uuid.UuidGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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

    init {
        scope.launch {
            launch {
                localDataStorage.currentLanguage.collect { value ->
                    val presentBehavior = messages.find {
                        it.role == Role.System &&
                                it.messageType == MessageType.LANGUAGE
                    }
                    val messageToSend = "Let's talk in ${value.title}"
                    if (presentBehavior == null) {
                        val lngId = uuidGenerator.getUuid4()
                        messages.add(
                            Message(
                                id = lngId,
                                text = messageToSend,
                                role = Role.System,
                                messageType = MessageType.LANGUAGE
                            )
                        )
                    } else {
                        val newBehavior = presentBehavior.copy(text = messageToSend)
                        messages[messages.indexOf(presentBehavior)] = newBehavior
                    }
                }
            }
            launch {
                localDataStorage.behavior.collect { value ->
                    val presentBehavior = messages.find {
                        it.role == Role.System &&
                                it.messageType == MessageType.BEHAVIOR
                    }
                    if (presentBehavior == null) {
                        val behaviorId = uuidGenerator.getUuid4()
                        messages.add(
                            Message(
                                id = behaviorId,
                                text = value,
                                role = Role.System,
                                messageType = MessageType.BEHAVIOR
                            )
                        )
                    } else {
                        val newBehavior = presentBehavior.copy(text = value)
                        messages[messages.indexOf(presentBehavior)] = newBehavior
                    }
                }
            }
            launch {
                localDataStorage.aiAnswerFixPrompt.collect { value ->
                    val presentBehavior = messages.find {
                        it.role == Role.System &&
                                it.messageType == MessageType.AI_FIX
                    }
                    if (presentBehavior == null) {
                        val aiFixId = uuidGenerator.getUuid4()
                        messages.add(
                            Message(
                                id = aiFixId,
                                text = value,
                                role = Role.System,
                                messageType = MessageType.AI_FIX
                            )
                        )
                    } else {
                        val newBehavior = presentBehavior.copy(text = value)
                        messages[messages.indexOf(presentBehavior)] = newBehavior
                    }
                }
            }
        }
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

    override suspend fun getModels(): Result<List<String>> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                Result.success(service.models().map { it.id.id })
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                logging.e { e }
                Result.failure(e)
            }
        }

    override suspend fun sendMessage(msg: String, msgId: String): Result<ResultMessage> =
        withContext(Dispatchers.IO) {
            try {
                val service = getOpenAi() ?: throw OpenAiEmptyApiKeyException()
                messages.add(Message(msgId, msg, Role.User))
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
                val resultMessageId = uuidGenerator.getUuid4()
                messages.add(
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
                return@withContext Result.success(resultMessage)
            } catch (e: CancellationException) {
                removeMessageById(msgId)
                throw e
            } catch (e: Exception) {
                removeMessageById(msgId)
                return@withContext Result.failure(e)
            }
        }

    private suspend fun removeMessageById(id: String): Unit = withContext(Dispatchers.IO) {
        messages.removeAll {
            it.id == id
        }
    }

    private suspend fun createChatCompletionRequest(messages: List<ChatMessage>): ChatCompletionRequest =
        ChatCompletionRequest(
            temperature = localDataStorage.tempFlow.first(),
            model = ModelId(localDataStorage.currentGptModel.first()),
            messages = messages,
        )
}
