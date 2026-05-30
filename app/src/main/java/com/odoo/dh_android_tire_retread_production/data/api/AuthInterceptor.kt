package com.odoo.dh_android_tire_retread_production.data.api

import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        runBlocking {
            sessionManager.accessToken.firstOrNull()?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            sessionManager.stationSession.firstOrNull()?.let {
                requestBuilder.header("X-Station-Session", it)
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
