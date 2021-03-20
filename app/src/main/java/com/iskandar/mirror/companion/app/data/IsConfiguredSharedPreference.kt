package com.iskandar.mirror.companion.app.data

import android.content.Context

class IsConfiguredSharedPreference(context: Context) {

    private val PREFERENCE_NAME = "IsConfigured"
    private val PREFERENCE_KEY = "1"

    private val preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun getIsConfigured(): Boolean {
        return preference.getBoolean(PREFERENCE_KEY, false)
    }

    fun setIsConfigured(isConfigured: Boolean) {
        val editor = preference.edit()
        editor.putBoolean(PREFERENCE_KEY, isConfigured)
        editor.apply()
    }
}
