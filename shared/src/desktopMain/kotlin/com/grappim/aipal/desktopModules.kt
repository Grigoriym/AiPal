package com.grappim.aipal

import com.grappim.aipal.data.DesktopLocalDataStorage
import com.grappim.aipal.data.local.LocalDataStorage
import org.koin.dsl.module

fun localDataStorage() = module {
    single<LocalDataStorage> { DesktopLocalDataStorage() }
}
