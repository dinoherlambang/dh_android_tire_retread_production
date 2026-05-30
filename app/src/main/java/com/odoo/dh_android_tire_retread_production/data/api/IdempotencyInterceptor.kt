package com.odoo.dh_android_tire_retread_production.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class IdempotencyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Add Idempotency-Key to POST requests if not already present
        return if (request.method == "POST") {
            val builder = request.newBuilder()
            if (request.header("Idempotency-Key") == null) {
                builder.addHeader("Idempotency-Key", UUID.randomUUID().toString())
            }
            chain.proceed(builder.build())
        } else {
            chain.proceed(request)
        }
    }
}
