package com.odoo.dh_android_tire_retread_production.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
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
data class StationSessionResponse(
    val station_session: String,
    val station: StationData,
    val opened_at: String
)

@Serializable
data class StationData(
    val id: Int,
    val code: String,
    val name: String,
    val service_type: String? = null
)

@Serializable
data class QueueData(
    val station: StationData,
    val items: List<QueueItem>,
    val total: Int
)

@Serializable
data class QueueItem(
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
data class WorkorderDetailData(
    val station: StationData,
    val workorder: WorkorderDetail,
    val materials: List<MaterialLine>
)

@Serializable
data class WorkorderDetail(
    val id: Int,
    val wo_number: String,
    val paper_wo: String? = null,
    val serial_number: String? = null,
    val state: String,
    val customer_name: String? = null,
    val service_type: String? = null,
    val process_status: String,
    val process_result: String? = null,
    val can_done: Boolean
)

@Serializable
data class MaterialLine(
    val id: Int,
    val product_name: String,
    val quantity: Double,
    val uom: String,
    val unit_cost: Double,
    val total_cost: Double,
    val state: String,
    val notes: String? = null,
    val is_wastage: Boolean
)
