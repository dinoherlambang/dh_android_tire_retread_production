package com.odoo.dh_android_tire_retread_production

import android.content.Context
import androidx.room.Room
import com.odoo.dh_android_tire_retread_production.data.api.AuthInterceptor
import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.local.AppDatabase
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.repository.WorkorderRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    val sessionManager = SessionManager(context)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = AuthInterceptor(sessionManager)

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8069/api/v1/mobile/") // 10.0.2.2 for emulator to reach localhost
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(MobileStationApi::class.java)

    private val database = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "station_db"
    ).build()

    val workorderRepository = WorkorderRepository(api, database)
}
