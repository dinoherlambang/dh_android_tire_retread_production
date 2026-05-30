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
    private val STATION_NAME = stringPreferencesKey("station_name")
    private val EXPIRES_AT = stringPreferencesKey("expires_at")
    private val DEFAULT_MOBILE_HOME = stringPreferencesKey("default_mobile_home")
    private val CAN_VIEW_DASHBOARD = booleanPreferencesKey("can_view_dashboard")

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val stationSession: Flow<String?> = context.dataStore.data.map { it[STATION_SESSION] }
    val stationCode: Flow<String?> = context.dataStore.data.map { it[STATION_CODE] }
    val stationName: Flow<String?> = context.dataStore.data.map { it[STATION_NAME] }
    val defaultMobileHome: Flow<String?> = context.dataStore.data.map { it[DEFAULT_MOBILE_HOME] }
    val canViewDashboard: Flow<Boolean> = context.dataStore.data.map { it[CAN_VIEW_DASHBOARD] ?: false }

    suspend fun saveAuthToken(token: String, expiresAt: String, defaultHome: String, canViewDashboard: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
            preferences[EXPIRES_AT] = expiresAt
            preferences[DEFAULT_MOBILE_HOME] = defaultHome
            preferences[CAN_VIEW_DASHBOARD] = canViewDashboard
        }
    }

    suspend fun saveStationSession(session: String, code: String, name: String) {
        context.dataStore.edit { preferences ->
            preferences[STATION_SESSION] = session
            preferences[STATION_CODE] = code
            preferences[STATION_NAME] = name
        }
    }

    suspend fun clearStationSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(STATION_SESSION)
            preferences.remove(STATION_CODE)
            preferences.remove(STATION_NAME)
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
