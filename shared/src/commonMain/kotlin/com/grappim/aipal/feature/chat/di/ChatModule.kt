package com.grappim.aipal.feature.chat.di

import com.grappim.aipal.feature.chat.ChatViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val chatModule = module {
    viewModelOf(::ChatViewModel)
}
