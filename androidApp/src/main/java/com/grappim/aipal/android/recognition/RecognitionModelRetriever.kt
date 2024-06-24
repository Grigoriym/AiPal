package com.grappim.aipal.android.recognition

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.vosk.Model
import org.vosk.android.StorageService

interface RecognitionModelRetriever {
    fun flowFromModel(): Flow<Model>
}

class RecognitionModelRetrieverImpl(
    private val appContext: Context,
) : RecognitionModelRetriever {
    override fun flowFromModel(): Flow<Model> =
        callbackFlow {
            println("Starting to unpack model")
            StorageService.unpack(
                appContext,
                "vosk-model-small-de-0.15",
                "model",
                { model ->
                    println("Model unpacked successfully")
                    trySendBlocking(model)
                        .onFailure {
                            println("Failed to send model")
                        }
                    channel.close()
                },
                { exception ->
                    println(exception)
                    cancel(CancellationException("Model retrieval error", exception))
                },
            )
            awaitClose()
        }
}
