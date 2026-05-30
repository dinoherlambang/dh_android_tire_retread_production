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
    val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val params = mapOf(
                    "login" to username.trim(),
                    "password" to password.trim(),
                    "device_name" to android.os.Build.MODEL,
                    "device_id" to "android_id_placeholder" // TODO: Get actual ID if possible
                )
                val response = authRepository.login(params)
                if (response.success && response.data != null) {
                    val data = response.data
                    sessionManager.saveAuthToken(
                        data.access_token,
                        data.expires_at,
                        data.roles.default_mobile_home,
                        data.roles.can_view_dashboard
                    )
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error(response.message ?: "Login failed")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
