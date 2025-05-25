package com.example.fitnesstracker.ui.screens.weight

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun WeightLogScreen(
    navController: NavHostController,
    viewModel: WeightViewModel = hiltViewModel() // Placeholder ViewModel
) {
    // TODO: Implement weight log UI: Manually log weight, view weight history graph
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Weight Log Screen")
    }
}
