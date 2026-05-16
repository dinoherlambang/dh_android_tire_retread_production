package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.ApiEnvelope
import com.odoo.dh_android_tire_retread_production.data.api.JsonRpcEnvelope
import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.local.AppDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Response

class WorkorderRepositoryTest {

    @Mock
    lateinit var mockApi: MobileStationApi
    
    @Mock
    lateinit var mockDatabase: AppDatabase

    lateinit var repository: WorkorderRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = WorkorderRepository(mockApi, mockDatabase)
    }

    @Test
    fun `handleResponse should return result when successful`() = runBlocking {
        // This test would normally test a private method, so we test it via a public one like login
        val expectedData = ApiEnvelope(
            success = true,
            message = "Success",
            data = null,
            server_time = "2023-01-01",
            api_version = "1.0"
        )
        val jsonRpcResponse = JsonRpcEnvelope(result = expectedData)
        val response = Response.success(jsonRpcResponse)

        `when`(mockApi.login(emptyMap())).thenReturn(response)

        val result = repository.login(emptyMap())
        assertEquals(expectedData, result)
    }
}
