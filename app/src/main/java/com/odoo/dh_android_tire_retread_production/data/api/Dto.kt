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
// Most other DTOs have been moved to com.odoo.dh_android_tire_retread_production.data.model
