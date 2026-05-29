package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val api: MobileStationApi
) {
    suspend fun getMasterData(): ApiResponse<MasterDataResponse> {
        val response = api.getMasterData()
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = false, message = "Empty response")
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun getStations(): ApiResponse<MasterDataResponse> {
        val response = api.getStations()
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = false, message = "Empty response")
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun openSession(params: Map<String, String>): ApiResponse<StationSessionResponse> {
        val response = api.openSession(params)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = false, message = "Empty response")
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun closeSession(params: Map<String, String> = emptyMap()): ApiResponse<Unit> {
        val response = api.closeSession(params)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = true)
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun heartbeat(): ApiResponse<Unit> {
        val response = api.heartbeat()
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = true)
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }
}
