package com.odoo.dh_android_tire_retread_production.data.repository

import com.odoo.dh_android_tire_retread_production.data.api.MobileStationApi
import com.odoo.dh_android_tire_retread_production.data.model.ApiResponse
import com.odoo.dh_android_tire_retread_production.data.model.LoginResponse
import com.odoo.dh_android_tire_retread_production.data.model.UserData
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
    fun `login should return ApiResponse when successful`() = runBlocking {
        val loginData = LoginResponse(
            access_token = "token",
            token_type = "Bearer",
            expires_at = "2023-12-31",
            user = UserData(1, "Test User", "test")
        )
        val expectedResponse = ApiResponse(
            success = true,
            message = "Success",
            data = loginData
        )
        val response = Response.success(expectedResponse)

        val params = mapOf("login" to "test", "password" to "pass")
        `when`(mockApi.login(params)).thenReturn(response)

        val result = repository.login(params)
        assertEquals(expectedResponse, result)
    }
}
