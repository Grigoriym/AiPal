package com.grappim.aipal.di

import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.uuid.UuidGenerator
import com.grappim.aipal.feature.chat.ChatViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel {
        ChatViewModel(
            aiPalRepo = get<AiPalRepo>(),
            localDataStorage = get<LocalDataStorage>(),
            uuidGenerator = get<UuidGenerator>()
        )
    }
}
