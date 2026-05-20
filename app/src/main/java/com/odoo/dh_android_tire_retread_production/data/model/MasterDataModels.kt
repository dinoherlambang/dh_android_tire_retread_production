package com.odoo.dh_android_tire_retread_production.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MasterDataResponse(
    val stations: List<StationData>,
    val customers: List<CustomerData>? = null,
    val tires: List<TireData>? = null,
    val operators: List<OperatorData>? = null
)

@Serializable
data class CustomerData(
    val id: Int,
    val name: String
)

@Serializable
data class TireData(
    val id: Int,
    val name: String,
    val brand: String? = null
)

@Serializable
data class OperatorData(
    val id: Int,
    val name: String
)
