package com.grappim.aipal.android.recognition.android

import android.content.Intent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import androidx.annotation.MainThread

interface AndroidSpeechRecognizerWrapper {
    val speechRecognizer: SpeechRecognizer

    fun isRecognitionAvailable(): Boolean

    @MainThread
    fun startListening(intent: Intent)

    @MainThread
    fun setRecognitionListener(listener: RecognitionListener)

    @MainThread
    fun stopListening()

    @MainThread
    fun cancel()
}
