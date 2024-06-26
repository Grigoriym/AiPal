package com.grappim.aipal

import android.app.Application
import com.grappim.aipal.di.initKoin

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this)
    }
}