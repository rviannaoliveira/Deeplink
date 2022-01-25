package com.rviannaoliveira.deeplink

import android.app.Application
import com.rviannaoliveira.deeplink.BuildConfig
import com.rviannaoliveira.deeplink.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger

class CustomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(org.koin.core.logger.Level.ERROR) else EmptyLogger()
            androidContext(this@CustomApplication)
            modules(AppModule.instance)
        }
    }
}