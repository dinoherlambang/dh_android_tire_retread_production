package com.odoo.dh_android_tire_retread_production.data.api

import okhttp3.Interceptor
import okhttp3.Response
import java.util.UUID

class IdempotencyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Only add Idempotency-Key for POST requests that are likely to mutate data
        // Based on the docs: "All POST write endpoints"
        if (request.method == "POST") {
            val idempotencyKey = "${System.currentTimeMillis()}-${UUID.randomUUID()}"
            val newRequest = request.newBuilder()
                .header("Idempotency-Key", idempotencyKey)
                .build()
            return chain.proceed(newRequest)
        }
        
        return chain.proceed(request)
    }
}
