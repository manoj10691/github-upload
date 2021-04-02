package com.vportals.app.service

import android.content.Context
import android.net.ConnectivityManager

object NetworkHelper {
    fun hasNetworkAccess(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return try {
            val networkInfo = cm.activeNetworkInfo
            networkInfo != null && networkInfo.isConnectedOrConnecting
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
