package com.imgoingonanadventure

import android.app.Application
import com.imgoingonanadventure.di.AppModule
import com.imgoingonanadventure.di.AppModuleImpl

class App: Application() {

    //todo утечка?
    companion object {
        lateinit var appModule: AppModule
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}
