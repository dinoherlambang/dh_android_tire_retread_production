package com.odoo.dh_android_tire_retread_production.data.api

import com.odoo.dh_android_tire_retread_production.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface MobileStationApi {

    @POST("api/v1/mobile/auth/login")
    suspend fun login(@Body params: Map<String, String>): Response<ApiResponse<LoginResponse>>

    @POST("api/v1/mobile/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("api/v1/mobile/station/session/open")
    suspend fun openSession(@Body params: Map<String, String>): Response<ApiResponse<StationSessionResponse>>

    @POST("api/v1/mobile/station/session/heartbeat")
    suspend fun heartbeat(): Response<ApiResponse<Unit>>

    @GET("api/v1/mobile/station/workorders")
    suspend fun getWorkorders(
        @Query("search") search: String? = null,
        @Query("state") state: String? = null
    ): Response<ApiResponse<QueueData>>

    @GET("api/v1/mobile/station/workorders/{workorder_id}")
    suspend fun getWorkorderDetail(
        @Path("workorder_id") workorderId: Int
    ): Response<ApiResponse<WorkorderDetailData>>

    @POST("api/v1/mobile/station/workorders/{workorder_id}/done")
    suspend fun markWorkorderDone(
        @Path("workorder_id") workorderId: Int,
        @Body params: Map<String, String>
    ): Response<ApiResponse<Unit>>

    @POST("api/v1/mobile/station/workorders/{workorder_id}/cancel")
    suspend fun cancelWorkorder(
        @Path("workorder_id") workorderId: Int
    ): Response<ApiResponse<Unit>>

    @POST("api/v1/mobile/station/workorders/resolve")
    suspend fun resolveWorkorder(@Body params: Map<String, String>): Response<ApiResponse<QueueItem>>

    @GET("api/v1/mobile/master-data")
    suspend fun getMasterData(
        @Query("updated_since") updatedSince: String? = null
    ): Response<ApiResponse<MasterDataResponse>>
}
