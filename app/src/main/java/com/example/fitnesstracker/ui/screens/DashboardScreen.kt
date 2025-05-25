package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.model.ActivitySummary
import com.example.fitnesstracker.model.DailyGoalProgress
import com.example.fitnesstracker.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit
import com.example.fitnesstracker.data.local.GoalType

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val activitySummary by viewModel.todayActivitySummary.collectAsState()
    val stepGoalProgress by viewModel.stepGoalProgress.collectAsState()
    val calorieGoalProgress by viewModel.calorieGoalProgress.collectAsState()
    // val recentActivities by viewModel.recentActivities.collectAsState() // For later

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.dashboard_greeting, "User"), // Placeholder name
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(R.string.dashboard_date_today, SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(activitySummary.date)),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SummaryCard(activitySummary)

        Spacer(modifier = Modifier.height(16.dp))

        stepGoalProgress?.let {
            GoalProgressCard(it, "Steps")
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        calorieGoalProgress?.let {
            GoalProgressCard(it, "Calories Burned")
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Placeholder for quick actions or recent activities
        // QuickActionsSection(navController)
        // Spacer(modifier = Modifier.height(16.dp))
        // RecentActivitiesSection(recentActivities, navController)
    }
}

@Composable
fun SummaryCard(summary: ActivitySummary) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_summary_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem(Icons.Filled.DirectionsRun, "${summary.totalSteps}", stringResource(R.string.steps_unit))
                SummaryItem(Icons.Filled.LocalFireDepartment, "${summary.totalCaloriesBurned}", stringResource(R.string.calories_unit_short))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                SummaryItem(Icons.Filled.DirectionsBike, String.format("%.2f", summary.totalDistanceKm), stringResource(R.string.km_unit))
                SummaryItem(Icons.Filled.FitnessCenter, formatDuration(summary.totalActiveTimeMillis), stringResource(R.string.active_time_unit))
            }
        }
    }
}

@Composable
fun SummaryItem(icon: ImageVector, value: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = unit, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = unit, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun GoalProgressCard(progress: DailyGoalProgress, titlePrefix: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$titlePrefix ${stringResource(id = R.string.goal_title_suffix)}",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                LinearProgressIndicator(
                    progress = progress.progress,
                    modifier = Modifier.weight(1f).height(12.dp),
                    color = if (progress.isAchieved) Color(0xFF66BB6A) else MaterialTheme.colorScheme.primary, // Green when achieved
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${progress.current.toInt()}/${progress.target.toInt()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (progress.isAchieved) {
                Text(
                    text = stringResource(R.string.goal_achieved_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF2E7D32) // Darker green for text
                )
            }
        }
    }
}

fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    return if (hours > 0) {
        String.format("%dh %02dm", hours, minutes)
    } else {
        String.format("%dm", minutes)
    }
}

// TODO: Implement QuickActionsSection and RecentActivitiesSection if desired
