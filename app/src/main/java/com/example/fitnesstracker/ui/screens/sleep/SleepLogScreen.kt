package com.example.fitnesstracker.ui.screens.sleep

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun SleepLogScreen(
    navController: NavHostController,
    viewModel: SleepViewModel = hiltViewModel() // Placeholder ViewModel
) {
    // TODO: Implement sleep log UI: Manually log sleep, view sleep history, sleep stats
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sleep Log Screen")
    }
}
