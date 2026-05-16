package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.*
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

    private fun <T> handleResponse(response: Response<JsonRpcEnvelope<ApiEnvelope<T>>>): ApiEnvelope<T> {
        if (response.isSuccessful) {
            val body = response.body()
            val result = body?.result
            if (result != null) {
                return result
            }
            throw Exception("Empty result in JSON-RPC response")
        } else {
            throw Exception("HTTP Error: ${response.code()} ${response.message()}")
        }
    }

    suspend fun login(params: Map<String, String>): ApiEnvelope<LoginData> {
        return handleResponse(api.login(params))
    }

    suspend fun openSession(params: Map<String, String>): ApiEnvelope<StationSessionData> {
        return handleResponse(api.openSession(params))
    }

    fun getWorkorders(
        search: String? = null
    ): Flow<List<StationQueueItem>> = flow {
        // Emit cached data first
        val cached = database.stationQueueDao().getAll().map { it.toDto() }
        emit(cached)

        try {
            val response = handleResponse(api.getWorkorders(search = search))
            if (response.success && (response.data != null)) {
                val items = response.data
                database.stationQueueDao().deleteAll()
                database.stationQueueDao().insertAll(items.map { it.toEntity() })
                emit(items)
            }
        } catch (e: Exception) {
            // Keep showing cached data on error
            android.util.Log.e("WorkorderRepository", "Error loading workorders", e)
        }
    }

    suspend fun getWorkorderDetail(workorderId: Int): ApiEnvelope<StationWorkorderDetailData> {
        return handleResponse(api.getWorkorderDetail(workorderId))
    }

    suspend fun resolveWorkorder(search: String): ApiEnvelope<StationWorkorderDetailData> {
        return handleResponse(api.resolveWorkorder(mapOf("search" to search)))
    }

    suspend fun markWorkorderDone(workorderId: Int): ApiEnvelope<Unit> {
        val idempotencyKey = "station-done-${System.currentTimeMillis()}-${UUID.randomUUID()}"
        return handleResponse(api.markWorkorderDone(workorderId, idempotencyKey))
    }

    suspend fun heartbeat(): ApiEnvelope<Unit> {
        return handleResponse(api.heartbeat())
    }

    private fun StationQueueItem.toEntity() = StationQueueEntity(
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

    private fun StationQueueEntity.toDto() = StationQueueItem(
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
