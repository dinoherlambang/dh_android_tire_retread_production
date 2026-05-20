package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueRepository @Inject constructor(
    private val api: MobileStationApi
) {
    suspend fun getQueue(search: String? = null, state: String? = null): ApiResponse<QueueData> {
        val response = api.getWorkorders(search, state)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = false, message = "Empty response")
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun getDetail(workorderId: Int): ApiResponse<WorkorderDetailData> {
        val response = api.getWorkorderDetail(workorderId)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = false, message = "Empty response")
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun markDone(workorderId: Int, result: String, notes: String? = null): ApiResponse<Unit> {
        val params = mutableMapOf("result" to result)
        notes?.let { params["notes"] = it }
        val response = api.markWorkorderDone(workorderId, params)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = true)
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }

    suspend fun cancel(workorderId: Int): ApiResponse<Unit> {
        val response = api.cancelWorkorder(workorderId)
        return if (response.isSuccessful) {
            response.body() ?: ApiResponse(success = true)
        } else {
            ApiResponse(success = false, message = response.message())
        }
    }
}
