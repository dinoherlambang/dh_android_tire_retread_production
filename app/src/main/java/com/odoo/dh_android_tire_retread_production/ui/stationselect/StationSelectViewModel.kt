package com.odoo.dh_android_tire_retread_production.ui.stationselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.model.StationData
import com.odoo.dh_android_tire_retread_production.data.repository.AuthRepository
import com.odoo.dh_android_tire_retread_production.data.repository.StationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StationSelectUiState {
    object Loading : StationSelectUiState()
    data class Success(val stations: List<StationData>) : StationSelectUiState()
    data class Error(val message: String) : StationSelectUiState()
    object SessionOpened : StationSelectUiState()
}

@HiltViewModel
class StationSelectViewModel @Inject constructor(
    private val stationRepository: StationRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<StationSelectUiState>(StationSelectUiState.Loading)
    val uiState: StateFlow<StationSelectUiState> = _uiState

    init {
        fetchStations()
    }

    fun fetchStations() {
        viewModelScope.launch {
            _uiState.value = StationSelectUiState.Loading
            try {
                val response = stationRepository.getMasterData()
                if (response.success && response.data != null) {
                    _uiState.value = StationSelectUiState.Success(response.data.stations)
                } else {
                    _uiState.value = StationSelectUiState.Error(response.message ?: "Failed to load stations")
                }
            } catch (e: Exception) {
                _uiState.value = StationSelectUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectStation(station: StationData, deviceName: String) {
        viewModelScope.launch {
            _uiState.value = StationSelectUiState.Loading
            try {
                val params = mapOf(
                    "station_code" to station.code,
                    "device_name" to deviceName,
                    "device_id" to "android_id_placeholder"
                )
                val response = authRepository.openSession(params)
                if (response.success && response.data != null) {
                    sessionManager.saveStationSession(response.data.station_session, station.code)
                    _uiState.value = StationSelectUiState.SessionOpened
                } else {
                    _uiState.value = StationSelectUiState.Error(response.message ?: "Failed to open session")
                }
            } catch (e: Exception) {
                _uiState.value = StationSelectUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
