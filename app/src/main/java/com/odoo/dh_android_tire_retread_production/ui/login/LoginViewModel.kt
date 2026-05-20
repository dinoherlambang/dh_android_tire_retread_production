package com.odoo.dh_android_tire_retread_production.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String, deviceName: String, stationCode: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val params = mapOf(
                    "login" to username.trim(),
                    "password" to password.trim(),
                    "device_name" to deviceName.trim(),
                    "device_id" to "android_id_placeholder" // TODO: Get actual ID
                )
                val response = authRepository.login(params)
                if (response.success && response.data != null) {
                    sessionManager.saveAuthToken(response.data.access_token, response.data.expires_at)
                    
                    // Automatically open session if station code is provided
                    if (stationCode.isNotBlank()) {
                        val sessionParams = mapOf(
                            "station_code" to stationCode.trim(),
                            "device_name" to deviceName.trim(),
                            "device_id" to "android_id_placeholder"
                        )
                        val sessionResponse = authRepository.openSession(sessionParams)
                        if (sessionResponse.success && sessionResponse.data != null) {
                            sessionManager.saveStationSession(sessionResponse.data.station_session, stationCode.trim())
                            _uiState.value = LoginUiState.Success
                        } else {
                            _uiState.value = LoginUiState.Error(sessionResponse.message ?: "Failed to open station session")
                        }
                    } else {
                        _uiState.value = LoginUiState.Success
                    }
                } else {
                    _uiState.value = LoginUiState.Error(response.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
