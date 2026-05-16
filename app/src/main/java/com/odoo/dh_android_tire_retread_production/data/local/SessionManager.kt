package com.odoo.dh_android_tire_retread_production.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(value) = prefs.edit().putString("access_token", value).apply()

    var stationSession: String?
        get() = prefs.getString("station_session", null)
        set(value) = prefs.edit().putString("station_session", value).apply()

    var stationCode: String?
        get() = prefs.getString("station_code", null)
        set(value) = prefs.edit().putString("station_code", value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
