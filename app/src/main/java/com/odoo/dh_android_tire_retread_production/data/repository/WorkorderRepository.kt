package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.*
import com.odoo.dh_android_tire_retread_production.data.model.*
import com.odoo.dh_android_tire_retread_production.data.local.AppDatabase
import com.odoo.dh_android_tire_retread_production.data.local.StationQueueEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import java.util.UUID

class WorkorderRepository(
    private val api: MobileStationApi,
    private val database: AppDatabase
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
            throw Exception("HTTP Error: ${response.code()} ${response.message()}")
        }
    }

    suspend fun login(params: Map<String, String>): ApiResponse<LoginResponse> {
        return handleResponse(api.login(params))
    }

    suspend fun openSession(params: Map<String, String>): ApiResponse<StationSessionResponse> {
        return handleResponse(api.openSession(params))
    }

    fun getWorkorders(
        search: String? = null
    ): Flow<List<QueueItem>> = flow {
        // Emit cached data first
        val cached = database.stationQueueDao().getAll().map { it.toDto() }
        emit(cached)

        try {
            val response = handleResponse(api.getWorkorders(search = search))
            if (response.success && (response.data != null)) {
                val items = response.data.items
                database.stationQueueDao().deleteAll()
                database.stationQueueDao().insertAll(items.map { it.toEntity() })
                emit(items)
            }
        } catch (e: Exception) {
            // Keep showing cached data on error
            android.util.Log.e("WorkorderRepository", "Error loading workorders", e)
        }
    }

    suspend fun getWorkorderDetail(workorderId: Int): ApiResponse<WorkorderDetailData> {
        return handleResponse(api.getWorkorderDetail(workorderId))
    }

    suspend fun resolveWorkorder(search: String): ApiResponse<QueueItem> {
        return handleResponse(api.resolveWorkorder(mapOf("search" to search)))
    }

    suspend fun markWorkorderDone(workorderId: Int): ApiResponse<Unit> {
        val idempotencyKey = "station-done-${System.currentTimeMillis()}-${UUID.randomUUID()}"
        return handleResponse(api.markWorkorderDone(workorderId, mapOf("idempotency_key" to idempotencyKey)))
    }

    suspend fun heartbeat(): ApiResponse<Unit> {
        return handleResponse(api.heartbeat())
    }

    private fun QueueItem.toEntity() = StationQueueEntity(
        workorder_id = workorder_id,
        wo_number = wo_number,
        paper_wo = paper_wo,
        serial_number = serial_number,
        customer_name = customer_name,
        service_type = service_type,
        station_status = station_status,
        station_result = station_result,
        last_update = last_update
    )

    private fun StationQueueEntity.toDto() = QueueItem(
        workorder_id = workorder_id,
        wo_number = wo_number,
        paper_wo = paper_wo,
        serial_number = serial_number,
        customer_name = customer_name,
        service_type = service_type,
        station_status = station_status,
        station_result = station_result,
        last_update = last_update
    )
}
