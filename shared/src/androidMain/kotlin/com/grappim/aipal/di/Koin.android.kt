package com.grappim.aipal.di

import android.content.Context
import org.koin.android.ext.koin.androidContext

fun initKoin(context: Context) = initKoin {
    androidContext(context)
}