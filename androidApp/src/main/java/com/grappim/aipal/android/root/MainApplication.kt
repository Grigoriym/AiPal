package com.grappim.aipal.android.root

import android.app.Application
import android.content.Context
import com.grappim.aipal.android.BuildConfig
import com.grappim.aipal.android.core.dataStoreModule
import com.grappim.aipal.android.core.jsonModule
import com.grappim.aipal.android.core.viewModelsModule
import com.grappim.aipal.android.recognition.AndroidRecognitionManager
import com.grappim.aipal.android.recognition.RecognitionManagerImpl
import com.grappim.aipal.android.recognition.RecognitionMessageDecoder
import com.grappim.aipal.android.recognition.RecognitionMessageDecoderImpl
import com.grappim.aipal.android.recognition.RecognitionModelRetriever
import com.grappim.aipal.android.recognition.RecognitionModelRetrieverImpl
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.repo.AiPalRepoImpl
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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
                single<AiPalRepo> { AiPalRepoImpl(get<LocalDataStorage>()) }

                single<AndroidRecognitionManager> { RecognitionManagerImpl(get<RecognitionMessageDecoder>()) }
                factory<RecognitionModelRetriever> { RecognitionModelRetrieverImpl(get<Context>()) }
                factory<RecognitionMessageDecoder> { RecognitionMessageDecoderImpl(get<Json>()) }
            }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModules, jsonModule, dataStoreModule, viewModelsModule)
        }
    }
}
