package com.odoo.dh_android_tire_retread_production

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.odoo.dh_android_tire_retread_production.data.local.SessionManager
import com.odoo.dh_android_tire_retread_production.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var binding: ActivityMainBinding
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        // Keep the splash screen on-screen until we've decided which screen to show
        splashScreen.setKeepOnScreenCondition { !isReady }
        
        setTheme(R.style.Theme_Dh_android_tire_retread_production)
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        lifecycleScope.launch {
            val token = sessionManager.accessToken.firstOrNull()
            val session = sessionManager.stationSession.firstOrNull()
            
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

            // Decide start destination based on session
            navGraph.setStartDestination(
                when {
                    token == null -> R.id.loginFragment
                    session == null -> R.id.stationSelectFragment
                    else -> R.id.queueFragment
                }
            )
            
            navController.graph = navGraph
            isReady = true
        }
    }
}
