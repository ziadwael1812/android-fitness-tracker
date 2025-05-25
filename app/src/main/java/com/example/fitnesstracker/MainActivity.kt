package com.example.fitnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fitnesstracker.ui.components.BottomNavigationBar
import com.example.fitnesstracker.ui.navigation.AppNavigation
import com.example.fitnesstracker.ui.navigation.Screen
import com.example.fitnesstracker.ui.navigation.bottomNavScreens
import com.example.fitnesstracker.ui.theme.FitnessTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessTrackerTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Determine the current screen's title
                val currentScreen = bottomNavScreens.find { it.route == currentRoute } 
                    ?: Screen.ActivityDetail // Default or find based on non-bottom nav routes too
                
                // A more robust way to get titles for all screens, not just bottom nav
                val titleResId = when (currentRoute) {
                    Screen.Dashboard.route -> R.string.title_dashboard
                    Screen.ActivityHistory.route -> R.string.title_activity_history
                    Screen.LogActivity.route -> R.string.title_log_activity
                    Screen.Goals.route -> R.string.title_goals
                    Screen.Settings.route -> R.string.title_settings
                    // Add cases for other screens like ActivityDetail, Profile etc.
                    // Example for a screen with arguments:
                    // Screen.ActivityDetail.route -> R.string.title_activity_detail 
                    else -> R.string.app_name // Default title
                }

                Scaffold(
                    topBar = {
                        // Only show TopAppBar for screens that are part of the bottom navigation
                        // or other specific screens where a top bar is desired.
                        if (bottomNavScreens.any { it.route == currentRoute }) {
                            TopAppBar(
                                title = { Text(stringResource(id = titleResId)) },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    },
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) {
                    innerPadding -> AppNavigation(navController = navController, innerPadding = innerPadding)
                }
            }
        }
    }
}
