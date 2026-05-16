package com.odoo.dh_android_tire_retread_production.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.api.StationQueueItem
import com.odoo.dh_android_tire_retread_production.data.api.StationWorkorderDetailData
import com.odoo.dh_android_tire_retread_production.data.repository.WorkorderRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WorkorderListUiState {
    object Loading : WorkorderListUiState()
    data class Success(val workorders: List<StationQueueItem>) : WorkorderListUiState()
    data class Error(val message: String) : WorkorderListUiState()
    data class NavigateToDetail(val data: StationWorkorderDetailData) : WorkorderListUiState()
}

class WorkorderListViewModel(private val repository: WorkorderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkorderListUiState>(WorkorderListUiState.Loading)
    val uiState: StateFlow<WorkorderListUiState> = _uiState

    private var heartbeatJob: Job? = null

    init {
        loadWorkorders()
        startHeartbeat()
    }

    fun loadWorkorders(search: String? = null) {
        viewModelScope.launch {
            _uiState.value = WorkorderListUiState.Loading
            repository.getWorkorders(search).collect { items ->
                _uiState.value = WorkorderListUiState.Success(items)
            }
        }
    }

    fun resolveWorkorder(search: String) {
        viewModelScope.launch {
            try {
                val response = repository.resolveWorkorder(search)
                if (response.success && response.data != null) {
                    _uiState.value = WorkorderListUiState.NavigateToDetail(response.data)
                } else {
                    // Show error or just keep list
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = viewModelScope.launch {
            while (true) {
                delay(60000) // 60 seconds
                try {
                    repository.heartbeat()
                } catch (e: Exception) {
                    // Heartbeat failed, might be logged out
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        heartbeatJob?.cancel()
    }
}
