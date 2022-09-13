package org.firmanmardiyanto.storyapp

import android.app.Application
import org.firmanmardiyanto.core.di.databaseModule
import org.firmanmardiyanto.core.di.networkModule
import org.firmanmardiyanto.core.di.repositoryModule
import org.firmanmardiyanto.storyapp.di.useCaseModule
import org.firmanmardiyanto.storyapp.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger(Level.NONE)
            modules(
                listOf(
                    useCaseModule,
                    databaseModule,
                    networkModule,
                    repositoryModule,
                    viewModelModule
                )
            )
        }
    }
}