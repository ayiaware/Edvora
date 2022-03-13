package com.ayia.rider

import android.app.Application
import com.ayia.rider.data.DataRepository
import timber.log.Timber

class RiderApp : Application() {

    companion object {
        private val TAG: String =
            GLOBAL_TAG + " " + RiderApp::class.java.simpleName
        @get:Synchronized
       lateinit var instance: RiderApp private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }

    fun getRepository(): DataRepository {
        return DataRepository()
    }


}