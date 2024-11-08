package com.grappim.aipal.android.core

import com.grappim.aipal.android.feature.chat.ChatViewModel
import com.grappim.aipal.android.feature.settings.SettingsViewModel
import com.grappim.aipal.android.feature.settings.ai.AiSettingsViewModel
import com.grappim.aipal.android.feature.settings.apiKeys.ApiKeysViewModel
import com.grappim.aipal.android.feature.stt.SttSettingsViewModel
import com.grappim.aipal.android.root.MainViewModel
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.recognition.STTFactory
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.repo.DbRepo
import com.grappim.aipal.data.uuid.UuidGenerator
import com.grappim.aipal.feature.prompts.PromptsViewModel
import com.grappim.aipal.feature.settings.language.LanguageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule =
    module {
        viewModel {
            ChatViewModel(
                get<AiPalRepo>(),
                get<LocalDataStorage>(),
                get<STTFactory>(),
                get<UuidGenerator>()
            )
        }
        viewModel {
            SettingsViewModel(
                get<LocalDataStorage>(),
            )
        }
        viewModel {
            MainViewModel(get<LocalDataStorage>())
        }
        viewModel {
            PromptsViewModel(get<LocalDataStorage>(), get<DbRepo>())
        }
        viewModel {
            ApiKeysViewModel(get<LocalDataStorage>(), get<AiPalRepo>())
        }
        viewModel {
            AiSettingsViewModel(get<LocalDataStorage>(), get<AiPalRepo>())
        }
        viewModel {
            SttSettingsViewModel(get<LocalDataStorage>(), get<STTFactory>())
        }
        viewModel {
            LanguageViewModel(get<LocalDataStorage>(), get<STTFactory>(), get<DbRepo>())
        }
    }
