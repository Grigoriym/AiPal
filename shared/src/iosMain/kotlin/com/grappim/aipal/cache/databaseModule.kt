package com.grappim.aipal.cache

import org.koin.dsl.module

fun databaseModule() = module {
    single<DatabaseDriverFactory> {
        IOSDatabaseDriverFactory()
    }
}
