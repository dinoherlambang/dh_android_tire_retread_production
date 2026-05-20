package com.odoo.dh_android_tire_retread_production.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.model.WorkorderDetailData
import com.odoo.dh_android_tire_retread_production.data.repository.QueueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val data: WorkorderDetailData) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
    object ActionSuccess : DetailUiState()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: QueueRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workorderId: Int = checkNotNull(savedStateHandle["workorderId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        fetchDetail()
    }

    fun fetchDetail() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val response = repository.getDetail(workorderId)
                if (response.success && response.data != null) {
                    _uiState.value = DetailUiState.Success(response.data)
                } else {
                    _uiState.value = DetailUiState.Error(response.message ?: "Failed to load detail")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun markDone(result: String, notes: String? = null) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val response = repository.markDone(workorderId, result, notes)
                if (response.success) {
                    _uiState.value = DetailUiState.ActionSuccess
                } else {
                    _uiState.value = DetailUiState.Error(response.message ?: "Failed to mark done")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun cancel() {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val response = repository.cancel(workorderId)
                if (response.success) {
                    _uiState.value = DetailUiState.ActionSuccess
                } else {
                    _uiState.value = DetailUiState.Error(response.message ?: "Failed to cancel")
                }
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
