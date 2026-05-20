package com.odoo.dh_android_tire_retread_production

import android.content.Context
import androidx.room.Room
import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.local.AppDatabase
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.repository.WorkorderRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val workorderRepository: WorkorderRepository
    val sessionManager: SessionManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val sessionManager: SessionManager by lazy {
        SessionManager(context)
    }

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(context, AppDatabase::class.java, "dh_production_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    private val api: MobileStationApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8069/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(MobileStationApi::class.java)
    }

    override val workorderRepository: WorkorderRepository by lazy {
        WorkorderRepository(api, database)
    }
}
