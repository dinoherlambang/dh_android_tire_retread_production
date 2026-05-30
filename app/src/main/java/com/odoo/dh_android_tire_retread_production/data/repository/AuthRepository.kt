package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: MobileStationApi
) {
    private fun <T> handleResponse(response: retrofit2.Response<ApiResponse<T>>): ApiResponse<T> {
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                if (!body.success) {
                    throw Exception(body.message ?: "Unknown error")
                }
                return body
            }
            throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                try {
                    val json = org.json.JSONObject(errorBody)
                    val message = json.optString("message")
                    if (message.isNotBlank()) throw Exception(message)
                } catch (e: Exception) { }
            }
            throw Exception("HTTP Error: ${response.code()} ${response.message()}")
        }
    }

    suspend fun login(params: Map<String, String>): ApiResponse<LoginResponse> {
        return handleResponse(api.login(params))
    }

    suspend fun logout(): ApiResponse<Unit> {
        return handleResponse(api.logout())
    }

    suspend fun getMe(): ApiResponse<UserData> {
        return handleResponse(api.getMe())
    }
}
