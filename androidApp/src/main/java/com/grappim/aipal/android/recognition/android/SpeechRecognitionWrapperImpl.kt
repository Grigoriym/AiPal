package com.grappim.aipal.android.recognition.android

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.annotation.MainThread

class SpeechRecognitionWrapperImpl(
    private val context: Context,
) : SpeechRecognitionWrapper {
    override val speechRecognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    override fun isRecognitionAvailable(): Boolean = SpeechRecognizer.isRecognitionAvailable(context)

    @MainThread
    override fun setRecognitionListener(listener: RecognitionListener) {
        speechRecognizer.setRecognitionListener(listener)
    }

    @MainThread
    override fun startListening(intent: Intent) {
        speechRecognizer.startListening(intent)
    }

    @MainThread
    override fun stopListening() {
        speechRecognizer.stopListening()
    }

    @MainThread
    override fun cancel() {
        speechRecognizer.cancel()
        speechRecognizer.setRecognitionListener(null)
    }
}
