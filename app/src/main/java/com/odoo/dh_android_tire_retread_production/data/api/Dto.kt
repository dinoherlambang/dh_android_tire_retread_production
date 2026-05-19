package com.odoo.dh_android_tire_retread_production.data.api

import kotlinx.serialization.Serializable

@Serializable
data class JsonRpcEnvelope<T>(
    val jsonrpc: String = "2.0",
    val id: Int? = null,
    val result: T? = null,
    val error: JsonRpcError? = null
)

@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: JsonRpcErrorData? = null
)

@Serializable
data class JsonRpcErrorData(
    val name: String? = null,
    val debug: String? = null,
    val message: String? = null,
    val exception_type: String? = null
)

@Serializable
data class ApiEnvelope<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error_code: String? = null,
    val server_time: String,
    val api_version: String
)

@Serializable
data class LoginData(
    val access_token: String,
    val token_type: String,
    val expires_at: String,
    val user: UserData
)

@Serializable
data class UserData(
    val id: Int,
    val name: String,
    val login: String
)

@Serializable
data class StationSessionData(
    val station_session: String,
    val station: StationData,
    val device_id: String,
    val device_name: String,
    val opened_at: String
)

@Serializable
data class StationData(
    val id: Int,
    val code: String,
    val name: String
)

@Serializable
data class StationQueueItem(
    val workorder_id: Int,
    val wo_number: String,
    val paper_wo: String? = null,
    val serial_number: String? = null,
    val customer_name: String? = null,
    val service_type: String? = null,
    val station_status: String,
    val station_result: String? = null,
    val last_update: String
)

@Serializable
data class StationWorkorderDetailData(
    val station: StationData,
    val workorder: WorkorderDetail,
    val materials: List<MaterialLine>
)

@Serializable
data class WorkorderDetail(
    val id: Int,
    val name: String,
    val state: String,
    // Add other relevant fields if needed
)

@Serializable
data class MaterialLine(
    val id: Int,
    val product_id: Int,
    val product_name: String,
    val quantity: Double,
    val uom: String,
    val unit_cost: Double,
    val total_cost: Double,
    val state: String,
    val notes: String? = null,
    val is_wastage: Boolean
)
