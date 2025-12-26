package com.neki.android.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NekiApplication: Application() {
    override fun onCreate() {
        super.onCreate()

    }
}