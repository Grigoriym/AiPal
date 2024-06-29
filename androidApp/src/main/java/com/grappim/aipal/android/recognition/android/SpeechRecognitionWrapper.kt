package com.grappim.aipal.android.recognition.android

import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer

interface SpeechRecognitionWrapper {
    val speechRecognizer: SpeechRecognizer

    fun isRecognitionAvailable(): Boolean

    fun startListening(intent: Intent)

    fun setRecognitionListener(listener: RecognitionListener)

    fun stopListening()

    fun cancel()
}
