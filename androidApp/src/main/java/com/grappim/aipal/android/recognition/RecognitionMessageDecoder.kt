package com.grappim.aipal.android.recognition

import kotlinx.serialization.json.Json

interface RecognitionMessageDecoder {
    fun decode(message: String): String
}

class RecognitionMessageDecoderImpl(
    private val json: Json,
) : RecognitionMessageDecoder {
    override fun decode(message: String): String {
        val recognitionMessage = json.decodeFromString<RecognitionMessage>(message)
        return recognitionMessage.text
    }
}
