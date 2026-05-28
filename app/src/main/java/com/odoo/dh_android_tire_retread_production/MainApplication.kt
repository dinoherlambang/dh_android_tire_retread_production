package com.odoo.dh_android_tire_retread_production

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.odoo.dh_android_tire_retread_production.util.SessionHeartbeatHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var heartbeatHandler: SessionHeartbeatHandler

    override fun onCreate() {
        super.onCreate()
        // Force Light Mode globally to prevent text color issues on newer Android versions
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        heartbeatHandler.start()
    }
}
