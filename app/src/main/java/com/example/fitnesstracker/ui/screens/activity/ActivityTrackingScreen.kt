package com.example.fitnesstracker.ui.screens.activity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ActivityTrackingScreen(
    navController: NavHostController,
    viewModel: ActivityViewModel = hiltViewModel() // Placeholder ViewModel
) {
    // TODO: Implement activity tracking UI: Map, controls (start, stop, pause), stats display
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Activity Tracking Screen")
    }
}
