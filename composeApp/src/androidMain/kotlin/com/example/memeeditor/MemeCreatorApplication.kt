package com.example.memeeditor

import android.app.Application
import com.example.memeeditor.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MemeCreatorApplication : Application()  {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MemeCreatorApplication)
        }
    }
    }

