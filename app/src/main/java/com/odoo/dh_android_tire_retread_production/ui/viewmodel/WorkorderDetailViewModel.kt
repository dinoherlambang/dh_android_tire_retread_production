package com.odoo.dh_android_tire_retread_production.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.api.StationWorkorderDetailData
import com.odoo.dh_android_tire_retread_production.data.repository.WorkorderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class WorkorderDetailUiState {
    object Loading : WorkorderDetailUiState()
    data class Success(val data: StationWorkorderDetailData) : WorkorderDetailUiState()
    data class Error(val message: String) : WorkorderDetailUiState()
    object Done : WorkorderDetailUiState()
}

class WorkorderDetailViewModel(private val repository: WorkorderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkorderDetailUiState>(WorkorderDetailUiState.Loading)
    val uiState: StateFlow<WorkorderDetailUiState> = _uiState

    fun loadDetail(workorderId: Int) {
        viewModelScope.launch {
            _uiState.value = WorkorderDetailUiState.Loading
            try {
                val response = repository.getWorkorderDetail(workorderId)
                if (response.success && response.data != null) {
                    _uiState.value = WorkorderDetailUiState.Success(response.data)
                } else {
                    _uiState.value = WorkorderDetailUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = WorkorderDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun markAsDone(workorderId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.markWorkorderDone(workorderId)
                if (response.success) {
                    _uiState.value = WorkorderDetailUiState.Done
                } else {
                    _uiState.value = WorkorderDetailUiState.Error(response.message)
                }
            } catch (e: Exception) {
                _uiState.value = WorkorderDetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
