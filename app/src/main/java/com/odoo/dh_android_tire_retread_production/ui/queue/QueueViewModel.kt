package com.odoo.dh_android_tire_retread_production.ui.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.model.QueueData
import com.odoo.dh_android_tire_retread_production.data.repository.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class QueueUiState {
    object Loading : QueueUiState()
    data class Success(val data: QueueData) : QueueUiState()
    data class Error(val message: String) : QueueUiState()
}

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val repository: QueueRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QueueUiState>(QueueUiState.Loading)
    val uiState: StateFlow<QueueUiState> = _uiState

    private var refreshJob: Job? = null

    init {
        startAutoRefresh()
    }

    fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (isActive) {
                fetchQueue()
                delay(30 * 1000) // 30 seconds
            }
        }
    }

    suspend fun fetchQueue() {
        try {
            val response = repository.getQueue()
            if (response.success && response.data != null) {
                _uiState.value = QueueUiState.Success(response.data)
            } else {
                if (_uiState.value !is QueueUiState.Success) {
                    _uiState.value = QueueUiState.Error(response.message ?: "Failed to load queue")
                }
            }
        } catch (e: Exception) {
            if (_uiState.value !is QueueUiState.Success) {
                _uiState.value = QueueUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}
