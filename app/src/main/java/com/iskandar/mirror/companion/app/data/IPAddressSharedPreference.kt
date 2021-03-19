package com.iskandar.mirror.companion.app.data

import android.content.Context

class IPAddressSharedPreference(context: Context) {

    private val PREFERENCE_NAME = "IP_Address"
    private val PREFERENCE_KEY = "0"

    private val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getIPAddress(): String? {
        return preference.getString(PREFERENCE_KEY, "http://10.0.2.2:5000/")
    }

    fun setIPAddress(ipAddress: String) {
        val editor = preference.edit()
        editor.putString(PREFERENCE_KEY, ipAddress)
        editor.apply()
    }
}
