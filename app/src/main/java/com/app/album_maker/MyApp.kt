package com.app.album_maker

import android.app.Application
import com.app.album_maker.utils.AppPrefs
import io.reactivex.plugins.RxJavaPlugins
import vn.supenient.camera.CameraSetup

class MyApp: Application() {

    companion object {

        private lateinit var myAppInstance: MyApp

        fun get(): MyApp {
            return myAppInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        myAppInstance = this

        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
        }
    }
}