package com.odoo.dh_android_tire_retread_production

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.navOptions
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
        
        splashScreen.setKeepOnScreenCondition { !isReady }
        
        setTheme(R.style.Theme_Dh_android_tire_retread_production)
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupTopBar()
        setupBottomNav()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        lifecycleScope.launch {
            val token = sessionManager.accessToken.firstOrNull()
            val session = sessionManager.stationSession.firstOrNull()
            val defaultHome = sessionManager.defaultMobileHome.firstOrNull()
            
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

            // Dynamic start destination based on role and session
            navGraph.setStartDestination(
                when {
                    token == null -> R.id.loginFragment
                    defaultHome == "dashboard" -> R.id.dashboardFragment
                    session == null -> R.id.stationSelectFragment
                    else -> R.id.queueFragment
                }
            )
            
            navController.graph = navGraph
            isReady = true
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            sessionManager.clear()
            // Reset the entire navigation structure back to the Login screen
            setupNavigation()
        }
    }

    private fun setupTopBar() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.topBarContainer.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val currentDestination by navController.currentBackStackEntryAsState()
                val currentRoute = currentDestination?.destination?.id
                val stationName by sessionManager.stationName.collectAsState(initial = null)

                if (currentRoute != null && currentRoute != R.id.loginFragment) {
                    val title = when (currentRoute) {
                        R.id.stationSelectFragment -> "Stations"
                        R.id.dashboardFragment -> "Dashboard"
                        R.id.queueFragment -> "Work Orders"
                        R.id.profileFragment -> "Profile"
                        else -> "Tire Retread System"
                    }
                    
                    TopHeader(
                        title = title,
                        stationName = stationName,
                        onRefreshClick = {
                            // Refresh current logic
                        },
                        onProfileClick = {
                            navController.safeNavigate(R.id.profileFragment)
                        },
                        onLogoutClick = {
                            performLogout()
                        }
                    )
                }
            }
        }
    }

    private fun setupBottomNav() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavContainer.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val canViewDashboard by sessionManager.canViewDashboard.collectAsState(initial = false)
                val currentDestination by navController.currentBackStackEntryAsState()
                val currentRoute = currentDestination?.destination?.id

                // Hide bottom nav on login screen
                if (currentRoute != null && currentRoute != R.id.loginFragment) {
                    BottomNavigationBar(
                        navController = navController,
                        currentDestinationId = currentRoute,
                        canViewDashboard = canViewDashboard
                    )
                }
            }
        }
    }
}

@Composable
fun TopHeader(
    title: String,
    stationName: String?,
    onRefreshClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA))
            .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PrecisionManufacturing,
                    contentDescription = null,
                    tint = Color(0xFF1A237E),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E)
                )
            }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Profile") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        onClick = { 
                            showMenu = false
                            onProfileClick()
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color.Red) },
                        onClick = { 
                            showMenu = false
                            onLogoutClick()
                        }
                    )
                }
            }
        }

        // Station Info Card - Only show if we have a station session AND we are not on station select
        if (stationName != null && title != "Stations") {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1A237E),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT STATION",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PrecisionManufacturing,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stationName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable(onClick = onRefreshClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavController.currentBackStackEntryAsState(): State<androidx.navigation.NavBackStackEntry?> {
    val entry = remember { mutableStateOf<androidx.navigation.NavBackStackEntry?>(currentBackStackEntry) }
    DisposableEffect(this) {
        val callback = NavController.OnDestinationChangedListener { controller, _, _ ->
            entry.value = controller.currentBackStackEntry
        }
        addOnDestinationChangedListener(callback)
        onDispose {
            removeOnDestinationChangedListener(callback)
        }
    }
    return entry
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentDestinationId: Int?,
    canViewDashboard: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (canViewDashboard) {
                BottomNavItem(
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    isSelected = currentDestinationId == R.id.dashboardFragment,
                    onClick = { 
                        navController.safeNavigate(R.id.dashboardFragment)
                    }
                )
            }

            BottomNavItem(
                label = "Stations",
                icon = Icons.Default.PrecisionManufacturing,
                isSelected = currentDestinationId == R.id.stationSelectFragment,
                onClick = { 
                    navController.safeNavigate(R.id.stationSelectFragment)
                }
            )

            BottomNavItem(
                label = "Work Orders",
                icon = Icons.Default.ListAlt,
                isSelected = currentDestinationId == R.id.queueFragment,
                onClick = { 
                    navController.safeNavigate(R.id.queueFragment)
                }
            )
        }
    }
}

fun NavController.safeNavigate(resId: Int) {
    val currentDest = currentDestination?.id
    if (currentDest == resId) return
    
    val options = navOptions {
        graph.startDestinationId.let { startId ->
            popUpTo(startId) {
                saveState = true
            }
        }
        launchSingleTop = true
        restoreState = true
    }
    
    this.navigate(resId, null, options)
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF1A237E) else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color.Gray

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
