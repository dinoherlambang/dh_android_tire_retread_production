package com.odoo.dh_android_tire_retread_production.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.repository.WorkorderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val repository: WorkorderRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String, deviceName: String, stationCode: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val loginResponse = repository.login(mapOf(
                    "login" to username.trim(),
                    "password" to password.trim()
                ))
                if (loginResponse.success && loginResponse.data != null) {
                    sessionManager.saveAuthToken(loginResponse.data.access_token, loginResponse.data.expires_at)
                    
                    val sessionResponse = repository.openSession(mapOf(
                        "station_code" to stationCode.trim(),
                        "device_name" to deviceName.trim()
                    ))
                    
                    if (sessionResponse.success && sessionResponse.data != null) {
                        sessionManager.saveStationSession(sessionResponse.data.station_session, stationCode.trim())
                        _uiState.value = LoginUiState.Success
                    } else {
                        _uiState.value = LoginUiState.Error(sessionResponse.message ?: "Failed to open session")
                    }
                } else {
                    _uiState.value = LoginUiState.Error(loginResponse.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
