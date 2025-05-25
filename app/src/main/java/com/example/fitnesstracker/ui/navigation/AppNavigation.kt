package com.example.fitnesstracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnesstracker.ui.screens.activity.ActivityTrackingScreen // Placeholder
import com.example.fitnesstracker.ui.screens.dashboard.DashboardScreen // Placeholder
import com.example.fitnesstracker.ui.screens.goals.GoalsScreen // Placeholder
import com.example.fitnesstracker.ui.screens.history.HistoryScreen // Placeholder
import com.example.fitnesstracker.ui.screens.profile.ProfileScreen // Placeholder
import com.example.fitnesstracker.ui.screens.settings.SettingsScreen // Placeholder
import com.example.fitnesstracker.ui.screens.sleep.SleepLogScreen // Placeholder
import com.example.fitnesstracker.ui.screens.weight.WeightLogScreen // Placeholder

// Sealed class for defining navigation routes
sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object ActivityTracking : Screen("activity_tracking")
    object History : Screen("history") // Could have sub-routes for activity details
    object Goals : Screen("goals")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object SleepLog : Screen("sleep_log")
    object WeightLog : Screen("weight_log")
    // Add more screens as needed, e.g., object ActivityDetail: Screen("activity_detail/{activityId}")
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController) // Pass navController for further navigation
        }
        composable(Screen.ActivityTracking.route) {
            ActivityTrackingScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(Screen.Goals.route) {
            GoalsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.SleepLog.route) {
            SleepLogScreen(navController = navController)
        }
        composable(Screen.WeightLog.route) {
            WeightLogScreen(navController = navController)
        }
        // Example for a screen with arguments:
        // composable("activity_detail/{activityId}", arguments = listOf(navArgument("activityId") { type = NavType.LongType })) { backStackEntry ->
        //     val activityId = backStackEntry.arguments?.getLong("activityId")
        //     ActivityDetailScreen(navController = navController, activityId = activityId)
        // }
    }
}

// Placeholder Composable functions for screens (to be implemented in their respective packages)
@Composable fun ActivityTrackingScreen(navController: NavHostController) { /* TODO */ }
@Composable fun DashboardScreen(navController: NavHostController) { /* TODO */ }
@Composable fun GoalsScreen(navController: NavHostController) { /* TODO */ }
@Composable fun HistoryScreen(navController: NavHostController) { /* TODO */ }
@Composable fun ProfileScreen(navController: NavHostController) { /* TODO */ }
@Composable fun SettingsScreen(navController: NavHostController) { /* TODO */ }
@Composable fun SleepLogScreen(navController: NavHostController) { /* TODO */ }
@Composable fun WeightLogScreen(navController: NavHostController) { /* TODO */ }
