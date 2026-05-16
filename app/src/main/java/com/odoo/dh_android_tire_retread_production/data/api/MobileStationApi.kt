package com.odoo.dh_android_tire_retread_production.data.api

import retrofit2.Response
import retrofit2.http.*

interface MobileStationApi {

    @POST("auth/login")
    suspend fun login(@Body params: Map<String, String>): Response<JsonRpcEnvelope<ApiEnvelope<LoginData>>>

    @POST("auth/logout")
    suspend fun logout(): Response<JsonRpcEnvelope<ApiEnvelope<Unit>>>

    @POST("station/session/open")
    suspend fun openSession(@Body params: Map<String, String>): Response<JsonRpcEnvelope<ApiEnvelope<StationSessionData>>>

    @POST("station/session/heartbeat")
    suspend fun heartbeat(): Response<JsonRpcEnvelope<ApiEnvelope<Unit>>>

    @GET("station/workorders")
    suspend fun getWorkorders(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("search") search: String? = null,
        @Query("state") state: String? = null,
        @Query("updated_since") updatedSince: String? = null
    ): Response<JsonRpcEnvelope<ApiEnvelope<List<StationQueueItem>>>>

    @POST("station/workorders/resolve")
    suspend fun resolveWorkorder(@Body params: Map<String, String>): Response<JsonRpcEnvelope<ApiEnvelope<StationWorkorderDetailData>>>

    @GET("station/workorders/{workorder_id}")
    suspend fun getWorkorderDetail(
        @Path("workorder_id") workorderId: Int
    ): Response<JsonRpcEnvelope<ApiEnvelope<StationWorkorderDetailData>>>

    @POST("station/workorders/{workorder_id}/done")
    suspend fun markWorkorderDone(
        @Path("workorder_id") workorderId: Int,
        @Header("X-Idempotency-Key") idempotencyKey: String
    ): Response<JsonRpcEnvelope<ApiEnvelope<Unit>>>
}
