package com.upiyptk.pintarbiologi.function

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Pack {
    fun isOnline(context: Context): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(connectivity != null) {
            val capabilities = connectivity.getNetworkCapabilities(connectivity.activeNetwork)
            if(capabilities != null) {
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }
}