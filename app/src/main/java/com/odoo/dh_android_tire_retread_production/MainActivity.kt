package com.odoo.dh_android_tire_retread_production

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.odoo.dh_android_tire_retread_production.ui.navigation.NavGraph
import com.odoo.dh_android_tire_retread_production.ui.theme.Dh_android_tire_retread_productionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as MainApplication).container
        
        enableEdgeToEdge()
        setContent {
            Dh_android_tire_retread_productionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController, container = container)
                }
            }
        }
    }
}
