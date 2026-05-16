package com.odoo.dh_android_tire_retread_production.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.odoo.dh_android_tire_retread_production.AppContainer
import com.odoo.dh_android_tire_retread_production.ui.screens.LoginScreen
import com.odoo.dh_android_tire_retread_production.ui.screens.WorkorderDetailScreen
import com.odoo.dh_android_tire_retread_production.ui.screens.WorkorderListScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object WorkorderList : Screen("workorder_list")
    object WorkorderDetail : Screen("workorder_detail/{workorderId}") {
        fun createRoute(workorderId: Int) = "workorder_detail/$workorderId"
    }
}

@Composable
fun NavGraph(navController: NavHostController, container: AppContainer) {
    val startDestination = if (container.sessionManager.accessToken != null && container.sessionManager.stationSession != null) {
        Screen.WorkorderList.route
    } else {
        Screen.Login.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                container = container,
                onLoginSuccess = {
                    navController.navigate(Screen.WorkorderList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.WorkorderList.route) {
            WorkorderListScreen(
                container = container,
                onNavigateToDetail = { workorderId ->
                    navController.navigate(Screen.WorkorderDetail.createRoute(workorderId))
                },
                onLogout = {
                    container.sessionManager.clear()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.WorkorderList.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.WorkorderDetail.route,
            arguments = listOf(navArgument("workorderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val workorderId = backStackEntry.arguments?.getInt("workorderId") ?: return@composable
            WorkorderDetailScreen(
                container = container,
                workorderId = workorderId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
