package com.odoo.dh_android_tire_retread_production.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.data.repository.StationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionHeartbeatHandler @Inject constructor(
    private val stationRepository: StationRepository,
    private val sessionManager: SessionManager
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatJob: Job? = null

    fun start() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        startHeartbeatLoop()
    }

    override fun onStop(owner: LifecycleOwner) {
        stopHeartbeatLoop()
    }

    private fun startHeartbeatLoop() {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive) {
                val session = sessionManager.stationSession.firstOrNull()
                if (session != null) {
                    stationRepository.heartbeat()
                }
                delay(5 * 60 * 1000) // 5 minutes
            }
        }
    }

    private fun stopHeartbeatLoop() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
}
