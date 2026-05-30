package com.odoo.dh_android_tire_retread_production.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.model.QueueData
import com.odoo.dh_android_tire_retread_production.data.repository.AuthRepository
import com.odoo.dh_android_tire_retread_production.data.repository.QueueRepository
import com.odoo.dh_android_tire_retread_production.data.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

sealed class QueueUiState {
    object Loading : QueueUiState()
    data class Success(val data: QueueData) : QueueUiState()
    data class Error(val message: String) : QueueUiState()
    object LoggedOut : QueueUiState()
    object StationClosed : QueueUiState()
}

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val repository: QueueRepository,
    private val stationRepository: StationRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<QueueUiState>(QueueUiState.Loading)
    val uiState: StateFlow<QueueUiState> = _uiState

    private var refreshJob: Job? = null

    init {
        startAutoRefresh()
    }

    fun refresh() {
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                fetchQueue()
                delay(60 * 1000) // 1 minute auto-refresh
            }
        }
    }

    suspend fun fetchQueue() {
        try {
            // Get current session directly
            val session = sessionManager.stationSession.firstOrNull()
            
            val response = if (session != null) {
                repository.getQueue()
            } else {
                repository.getAllWorkorders()
            }
            
            if (response.success && response.data != null) {
                _uiState.value = QueueUiState.Success(response.data)
            } else {
                if (_uiState.value !is QueueUiState.Success) {
                    _uiState.value = QueueUiState.Error(response.message ?: "Failed to load work orders")
                }
            }
        } catch (e: Exception) {
            if (_uiState.value !is QueueUiState.Success) {
                _uiState.value = QueueUiState.Error(e.message ?: "Network error")
            }
        }
    }

    fun changeStation() {
        viewModelScope.launch {
            _uiState.value = QueueUiState.Loading
            try {
                stationRepository.closeSession()
                sessionManager.clearStationSession()
                _uiState.value = QueueUiState.StationClosed
            } catch (e: Exception) {
                sessionManager.clearStationSession()
                _uiState.value = QueueUiState.StationClosed
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = QueueUiState.Loading
            try {
                stationRepository.closeSession()
                authRepository.logout()
            } catch (e: Exception) {
                // Ignore
            } finally {
                sessionManager.clear()
                _uiState.value = QueueUiState.LoggedOut
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
