package com.grappim.aipal.android.core

import com.grappim.aipal.android.feature.chat.ChatViewModel
import com.grappim.aipal.android.feature.prompts.PromptsViewModel
import com.grappim.aipal.android.feature.settings.SettingsViewModel
import com.grappim.aipal.android.feature.settings.ai.AiSettingsViewModel
import com.grappim.aipal.android.feature.settings.apiKeys.ApiKeysViewModel
import com.grappim.aipal.android.recognition.RecognitionManager
import com.grappim.aipal.android.recognition.RecognitionModelRetriever
import com.grappim.aipal.android.root.MainViewModel
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.AiPalRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel {
        ChatViewModel(
            get<AiPalRepo>(),
            get<RecognitionManager>(),
            get<RecognitionModelRetriever>(),
        )
    }
    viewModel {
        SettingsViewModel(
            get<AiPalRepo>(),
            get<LocalDataStorage>(),
        )
    }
    viewModel {
        MainViewModel(get<LocalDataStorage>())
    }
    viewModel {
        PromptsViewModel(get<LocalDataStorage>())
    }
    viewModel {
        ApiKeysViewModel(get<LocalDataStorage>(), get<AiPalRepo>())
    }
    viewModel {
        AiSettingsViewModel(get<LocalDataStorage>(), get<AiPalRepo>())
    }
}