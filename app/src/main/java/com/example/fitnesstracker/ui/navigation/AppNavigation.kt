package com.example.fitnesstracker.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitnesstracker.ui.screens.ActivityDetailScreen // Placeholder
import com.example.fitnesstracker.ui.screens.ActivityHistoryScreen // Placeholder
import com.example.fitnesstracker.ui.screens.DashboardScreen // Placeholder
import com.example.fitnesstracker.ui.screens.EditGoalScreen // Placeholder
import com.example.fitnesstracker.ui.screens.GoalsScreen // Placeholder
import com.example.fitnesstracker.ui.screens.LogActivityScreen // Placeholder
import com.example.fitnesstracker.ui.screens.ProfileScreen // Placeholder
import com.example.fitnesstracker.ui.screens.SetGoalScreen // Placeholder
import com.example.fitnesstracker.ui.screens.SettingsScreen // Placeholder

@Composable
fun AppNavigation(
    navController: NavHostController,
    innerPadding: PaddingValues // To apply padding from Scaffold
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.ActivityHistory.route) {
            ActivityHistoryScreen(navController = navController)
        }
        composable(Screen.LogActivity.route) {
            LogActivityScreen(navController = navController)
        }
        composable(Screen.Goals.route) {
            GoalsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(
            route = Screen.ActivityDetail.route,
            arguments = listOf(navArgument("activityId") { type = NavType.LongType })
        ) {
            // val activityId = it.arguments?.getLong("activityId") ?: -1L // retrieve in ViewModel typically
            ActivityDetailScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(
            route = Screen.SetGoal.route,
            arguments = listOf(navArgument("goalType") { type = NavType.StringType; nullable = true })
        ) {
            // val goalType = it.arguments?.getString("goalType") // retrieve in ViewModel
            SetGoalScreen(navController = navController)
        }

        composable(
            route = Screen.EditGoal.route,
            arguments = listOf(navArgument("goalId") { type = NavType.LongType })
        ) {
             // val goalId = it.arguments?.getLong("goalId") ?: -1L // retrieve in ViewModel
            EditGoalScreen(navController = navController)
        }

        // Add more composable destinations for other screens
    }
}

// Note: Actual screen composables (DashboardScreen, etc.) will be created later.
// For now, they are just placeholder imports.
