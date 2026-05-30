package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueRepository @Inject constructor(
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

    suspend fun getQueue(search: String? = null, state: String? = null): ApiResponse<QueueData> {
        return handleResponse(api.getWorkorders(search, state))
    }

    suspend fun getAllWorkorders(search: String? = null, state: String? = null): ApiResponse<QueueData> {
        return handleResponse(api.getAllWorkorders(search, state))
    }

    suspend fun getDetail(workorderId: Int): ApiResponse<WorkorderDetailData> {
        return handleResponse(api.getWorkorderDetail(workorderId))
    }

    suspend fun resolveWorkorder(query: String): ApiResponse<QueueItem> {
        return handleResponse(api.resolveWorkorder(mapOf("query_text" to query)))
    }

    suspend fun markDone(workorderId: Int, result: String, notes: String? = null): ApiResponse<Unit> {
        val params = mutableMapOf("result" to result)
        notes?.let { params["notes"] = it }
        return handleResponse(api.markWorkorderDone(workorderId, params))
    }

    suspend fun cancel(workorderId: Int): ApiResponse<Unit> {
        return handleResponse(api.cancelWorkorder(workorderId))
    }
}
