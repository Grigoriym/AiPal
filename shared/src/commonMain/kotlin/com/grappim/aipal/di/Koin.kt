package com.grappim.aipal.di

import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.feature.chat.ChatViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule())
}

fun commonModule() = module {
    single {
        Json {
            isLenient = true
            prettyPrint = false
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    viewModel { ChatViewModel(get<AiPalRepo>()) }
}
