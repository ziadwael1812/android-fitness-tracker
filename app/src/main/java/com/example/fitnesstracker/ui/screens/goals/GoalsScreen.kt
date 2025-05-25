package com.example.fitnesstracker.ui.screens.goals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun GoalsScreen(
    navController: NavHostController,
    viewModel: GoalsViewModel = hiltViewModel() // Placeholder ViewModel
) {
    // TODO: Implement goals UI: Set, view, and edit daily/weekly fitness goals
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Goals Screen")
    }
}
