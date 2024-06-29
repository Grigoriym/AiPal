package com.grappim.aipal.android.root

import android.app.Application
import com.grappim.aipal.android.BuildConfig
import com.grappim.aipal.android.core.dataStoreModule
import com.grappim.aipal.android.core.jsonModule
import com.grappim.aipal.android.core.recognizersModule
import com.grappim.aipal.android.core.viewModelsModule
import com.grappim.aipal.data.local.LocalDataStorage
import com.grappim.aipal.data.repo.AiPalRepo
import com.grappim.aipal.data.repo.AiPalRepoImpl
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
            }

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(appModules, jsonModule, dataStoreModule, recognizersModule, viewModelsModule)
        }
    }
}
