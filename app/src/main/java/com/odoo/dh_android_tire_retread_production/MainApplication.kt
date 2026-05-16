package com.odoo.dh_android_tire_retread_production

import android.app.Application

class MainApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
