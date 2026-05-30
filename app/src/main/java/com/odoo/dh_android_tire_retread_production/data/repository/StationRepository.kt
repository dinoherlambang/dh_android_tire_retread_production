package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.*
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val api: MobileStationApi
) {
    private fun <T> handleResponse(response: Response<ApiResponse<T>>): ApiResponse<T> {
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

    suspend fun getMasterData(updatedSince: String? = null): ApiResponse<MasterDataResponse> {
        return handleResponse(api.getMasterData(updatedSince))
    }

    suspend fun getStations(): ApiResponse<MasterDataResponse> {
        // In the new API version, stations are part of master-data
        return handleResponse(api.getMasterData())
    }

    suspend fun getDashboard(
        dateRange: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): ApiResponse<DashboardData> {
        return handleResponse(api.getDashboard(dateRange, dateFrom, dateTo))
    }

    suspend fun openSession(params: Map<String, String>): ApiResponse<StationSessionResponse> {
        return handleResponse(api.openSession(params))
    }

    suspend fun closeSession(params: Map<String, String> = emptyMap()): ApiResponse<Unit> {
        return handleResponse(api.closeSession(params))
    }

    suspend fun heartbeat(): ApiResponse<Unit> {
        return handleResponse(api.heartbeat())
    }
}
