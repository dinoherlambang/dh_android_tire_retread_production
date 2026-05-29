package com.odoo.dh_android_tire_retread_production.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val STATION_SESSION = stringPreferencesKey("station_session")
    private val STATION_CODE = stringPreferencesKey("station_code")
    private val EXPIRES_AT = stringPreferencesKey("expires_at")

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val stationSession: Flow<String?> = context.dataStore.data.map { it[STATION_SESSION] }
    val stationCode: Flow<String?> = context.dataStore.data.map { it[STATION_CODE] }

    suspend fun saveAuthToken(token: String, expiresAt: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[EXPIRES_AT] = expiresAt
        }
    }

    suspend fun saveStationSession(session: String, code: String) {
        context.dataStore.edit { preferences ->
            preferences[STATION_SESSION] = session
            preferences[STATION_CODE] = code
        }
    }

    suspend fun clearStationSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(STATION_SESSION)
            preferences.remove(STATION_CODE)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
