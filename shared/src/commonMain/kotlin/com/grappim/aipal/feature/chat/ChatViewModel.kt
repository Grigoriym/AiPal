package com.grappim.aipal.feature.chat

import androidx.lifecycle.ViewModel
import com.grappim.aipal.data.repo.AiPalRepo
import org.lighthousegames.logging.logging

class ChatViewModel(
    private val aiPalRepo: AiPalRepo
) : ViewModel() {

    private val logging = logging()

    init {

    }
}
