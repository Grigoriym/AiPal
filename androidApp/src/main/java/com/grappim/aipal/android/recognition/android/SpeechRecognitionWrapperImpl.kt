package com.grappim.aipal.android.recognition.android

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer

class SpeechRecognitionWrapperImpl(
    private val context: Context,
) : SpeechRecognitionWrapper {
    override val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    override fun isRecognitionAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    override fun setRecognitionListener(listener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(listener)
    }

    override fun startListening(intent: Intent) {
        speechRecognizer.startListening(intent)
    }

    override fun stopListening() {
        speechRecognizer.stopListening()
    }

    override fun cancel() {
        speechRecognizer.cancel()
    }
}