package com.grappim.aipal.android.root

import android.app.Application
import com.grappim.aipal.android.BuildConfig
import com.grappim.aipal.android.core.dataStoreModule
import com.grappim.aipal.android.core.recognizersModule
import com.grappim.aipal.android.core.viewModelsModule
import com.grappim.aipal.cache.databaseModule
import com.grappim.aipal.data.repo.DbRepo
import com.grappim.aipal.di.appModule
import com.grappim.aipal.di.mobileLocalDataStorageModule
import com.grappim.aipal.di.repoModule
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.LogLevel

class MainApplication : Application() {

    private val applicationScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        KmLogging.setLogLevel(if (BuildConfig.DEBUG) LogLevel.Verbose else LogLevel.Off)

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                appModule,
                databaseModule,
                dataStoreModule,
                mobileLocalDataStorageModule(),
                repoModule(),
                recognizersModule,
                viewModelsModule
            )
        }

        applicationScope.launch {
            val repo = get<DbRepo>()
            repo.checkAndPrepopulateDatabase(BuildConfig.DEBUG)
        }
    }
}
