package com.raj.chase

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppApplication() : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AppApplication
            private set
    }
}