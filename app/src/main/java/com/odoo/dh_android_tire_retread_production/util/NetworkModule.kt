package com.odoo.dh_android_tire_retread_production.util

import com.odoo.dh_android_tire_retread_production.data.api.AuthInterceptor
import com.odoo.dh_android_tire_retread_production.data.api.IdempotencyInterceptor
import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        sessionManager: SessionManager,
        idempotencyInterceptor: IdempotencyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager))
            .addInterceptor(idempotencyInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideIdempotencyInterceptor(): IdempotencyInterceptor = IdempotencyInterceptor()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8069/") // Default Odoo dev address for emulator
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMobileStationApi(retrofit: Retrofit): MobileStationApi {
        return retrofit.create(MobileStationApi::class.java)
    }
}
