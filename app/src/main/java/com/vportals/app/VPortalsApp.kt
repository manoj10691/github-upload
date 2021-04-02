package com.vportals.app

import android.app.Application
import android.util.Log
import com.vportals.app.service.AppPreferences

class VPortalsApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
        if (!AppPreferences.firstRun) {
            AppPreferences.firstRun = true
            Log.d("VPortalsApp", "The value of our pref is: ${AppPreferences.firstRun}")
        }
    }
}