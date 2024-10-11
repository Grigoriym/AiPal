package com.grappim.aipal.cache

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single<DatabaseDriverFactory> {
        AndroidDatabaseDriverFactory(androidContext())
    }
}
