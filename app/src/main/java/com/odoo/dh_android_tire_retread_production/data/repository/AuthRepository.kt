package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class
AuthRepository @Inject constructor(
    private val api: MobileStationApi
) {
    suspend fun login(params: Map<String, String>): ApiResponse<LoginResponse> {
        val response = api.login(params)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = false, message = "Empty response")
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun logout(): ApiResponse<Unit> {
        val response = api.logout()
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = true)
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }
}
