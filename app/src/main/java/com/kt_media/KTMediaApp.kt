package com.kt_media

import android.app.Application
import com.kt_media.di.repositoryModule
import com.kt_media.di.useCaseModule
import com.kt_media.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class KTMediaApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KTMediaApp)
            androidLogger(Level.NONE)

            modules(
                useCaseModule,
                viewModelModule,
                repositoryModule
            )
        }
    }
}