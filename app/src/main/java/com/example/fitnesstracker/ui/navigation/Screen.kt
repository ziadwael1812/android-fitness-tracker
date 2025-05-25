package com.example.fitnesstracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.DonutLarge
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String? = null, val icon: ImageVector? = null, val selectedIcon: ImageVector? = null) {
    // Main Bottom Navigation Screens
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Outlined.DonutLarge, Icons.Filled.DonutLarge)
    object ActivityHistory : Screen("activity_history", "History", Icons.Outlined.History, Icons.Filled.History)
    object LogActivity : Screen("log_activity", "Log Activity", Icons.Outlined.DirectionsRun, Icons.Filled.DirectionsRun)
    object Goals : Screen("goals", "Goals", Icons.Outlined.Assessment, Icons.Filled.Assessment) // Or use a trophy icon
    object Settings : Screen("settings", "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)

    // Other Screens (not in bottom nav, typically)
    object ActivityDetail : Screen("activity_detail/{activityId}") {
        fun createRoute(activityId: Long) = "activity_detail/$activityId"
    }
    object Profile : Screen("profile", "Profile") // Could be part of Settings or its own tab
    object SetGoal : Screen("set_goal/{goalType}") { // Example: pass goal type as argument
        fun createRoute(goalType: String) = "set_goal/$goalType"
    }
    object EditGoal : Screen("edit_goal/{goalId}"){
        fun createRoute(goalId: Long) = "edit_goal/$goalId"
    }

    // Add more screens as needed, e.g., Onboarding, Login, etc.
}

// List for Bottom Navigation Bar
val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.ActivityHistory,
    Screen.LogActivity,
    Screen.Goals,
    Screen.Settings
)
