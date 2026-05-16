package com.odoo.dh_android_tire_retread_production.data.api

import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        sessionManager.accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        sessionManager.stationSession?.let {
            requestBuilder.addHeader("X-Station-Session", it)
        }

        return chain.proceed(requestBuilder.build())
    }
}
