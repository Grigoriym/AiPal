package com.grappim.aipal.android.root

import android.app.Application
import android.content.Context
import com.grappim.aipal.android.BuildConfig
import com.grappim.aipal.android.core.dataStoreModule
import com.grappim.aipal.android.core.jsonModule
import com.grappim.aipal.android.data.local.LocalDataStorage
import com.grappim.aipal.android.data.repo.AiPalRepo
import com.grappim.aipal.android.data.repo.AiPalRepoImpl
import com.grappim.aipal.android.data.service.OpenAiClient
import com.grappim.aipal.android.data.service.OpenAiClientImpl
import com.grappim.aipal.android.feature.chat.ChatViewModel
import com.grappim.aipal.android.feature.settings.SettingsViewModel
import com.grappim.aipal.android.recognition.RecognitionManager
import com.grappim.aipal.android.recognition.RecognitionManagerImpl
import com.grappim.aipal.android.recognition.RecognitionMessageDecoder
import com.grappim.aipal.android.recognition.RecognitionMessageDecoderImpl
import com.grappim.aipal.android.recognition.RecognitionModelRetriever
import com.grappim.aipal.android.recognition.RecognitionModelRetrieverImpl
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KmLogging.setLogLevel(if (BuildConfig.DEBUG) LogLevel.Verbose else LogLevel.Off)
        val appModules =
            module {
                single<OpenAiClient> { OpenAiClientImpl() }
                single<AiPalRepo> { AiPalRepoImpl(get<OpenAiClient>(), get<LocalDataStorage>()) }
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
                single<RecognitionManager> { RecognitionManagerImpl(get<RecognitionMessageDecoder>()) }
                factory<RecognitionModelRetriever> { RecognitionModelRetrieverImpl(get<Context>()) }
                factory<RecognitionMessageDecoder> { RecognitionMessageDecoderImpl(get<Json>()) }
            }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModules, jsonModule, dataStoreModule)
        }
    }
}
