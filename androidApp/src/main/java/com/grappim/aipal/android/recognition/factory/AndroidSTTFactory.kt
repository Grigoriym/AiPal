package com.grappim.aipal.android.recognition.factory

import com.grappim.aipal.android.recognition.android.AndroidSttManager
import com.grappim.aipal.android.recognition.vosk.VoskSttManager
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTFactory
import com.grappim.aipal.data.recognition.STTManager
import org.lighthousegames.logging.logging

class AndroidSTTFactory(
    private val androidSttManager: AndroidSttManager,
    private val voskSttManager: VoskSttManager
) : STTFactory {

    private val logging = logging()

    private var currentSTT: STTManager? = null

    override fun getSSTManager(currentSTTManager: CurrentSTTManager): STTManager {
        logging.d { "Now you have chosen: $currentSTTManager" }

        val newSTT = when (currentSTTManager) {
            CurrentSTTManager.Android -> androidSttManager
            CurrentSTTManager.Vosk -> voskSttManager
        }

        if (currentSTT != newSTT) {
            currentSTT?.cleanup()
            currentSTT = newSTT
            currentSTT?.initialize()
        }

        logging.d { "Now you are using: $currentSTT" }
        return requireNotNull(currentSTT)
    }
}
