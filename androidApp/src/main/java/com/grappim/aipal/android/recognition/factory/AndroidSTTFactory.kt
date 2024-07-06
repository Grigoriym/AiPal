package com.grappim.aipal.android.recognition.factory

import com.grappim.aipal.android.recognition.android.AndroidSSTManager
import com.grappim.aipal.android.recognition.vosk.VoskSttManager
import com.grappim.aipal.data.recognition.CurrentSTTManager
import com.grappim.aipal.data.recognition.STTManager
import com.grappim.aipal.data.recognition.STTFactory
import org.lighthousegames.logging.logging

class AndroidSTTFactory(
    private val androidSSTManagerFactory: () -> AndroidSSTManager,
    private val voskSttManagerFactory: () -> VoskSttManager
) : STTFactory {

    private var androidManager: AndroidSSTManager? = null
    private var voskManager: VoskSttManager? = null

    override fun getSSTManager(currentSTTManager: CurrentSTTManager): STTManager {
        logging().d { "Now you have chosen: $currentSTTManager" }
        val manager = when (currentSTTManager) {
            CurrentSTTManager.Android -> getAndroidManager()
            CurrentSTTManager.Vosk -> getVoskManager()
        }
        logging().d { "Now you are using: $manager" }
        return manager
    }

    private fun getAndroidManager(): AndroidSSTManager {
        voskManager?.cleanup()
        if (androidManager == null) {
            androidManager = androidSSTManagerFactory()
        }
        androidManager?.initialize()
        return androidManager!!
    }

    private fun getVoskManager(): VoskSttManager {
        androidManager?.cleanup()
        if (voskManager == null) {
            voskManager = voskSttManagerFactory()
        }
        voskManager?.initialize()
        return voskManager!!
    }
}
