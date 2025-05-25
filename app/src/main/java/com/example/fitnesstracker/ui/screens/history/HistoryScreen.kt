package com.example.fitnesstracker.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fitnesstracker.data.local.entity.ActivityRecord
import com.example.fitnesstracker.ui.navigation.Screen // Assuming you have a route for ActivityDetail
import com.example.fitnesstracker.util.formatDistance
import com.example.fitnesstracker.util.formatDuration
import com.example.fitnesstracker.util.toFormattedDateString
import com.example.fitnesstracker.util.toFormattedTimeString
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // var showDeleteDialog by remember { mutableStateOf<ActivityRecord?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Activity History") })
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.activities.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("No activities recorded yet. Go track one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.activities, key = { it.id }) { activity ->
                    ActivityHistoryItem(
                        activity = activity,
                        onClick = {
                            // TODO: Navigate to a detailed screen if one exists
                            // navController.navigate(Screen.ActivityDetail.route + "/${activity.id}")
                        },
                        onDelete = {
                            // TODO: Implement delete confirmation and viewModel call
                            // showDeleteDialog = activity 
                        }
                    )
                }
            }
        }
    }

    /* TODO: Implement Delete Confirmation Dialog
    showDeleteDialog?.let {
        activityToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Activity?") },
            text = { Text("Are you sure you want to delete this activity? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { 
                        // viewModel.deleteActivity(activityToDelete)
                        showDeleteDialog = null 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }
    */
}

@Composable
fun ActivityHistoryItem(
    activity: ActivityRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit // Placeholder for delete action
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = when (activity.type.lowercase(Locale.getDefault())) {
                    "running" -> Icons.Filled.DirectionsRun
                    "walking" -> Icons.Filled.DirectionsWalk
                    "cycling" -> Icons.Filled.DirectionsBike
                    "hiking" -> Icons.Filled.Terrain
                    else -> Icons.Filled.FitnessCenter
                },
                contentDescription = activity.type,
                modifier = Modifier.size(48.dp).padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(activity.type, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "${activity.startTime.toFormattedDateString()} at ${activity.startTime.toFormattedTimeString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Time: ${activity.durationMillis.formatDuration()}", style = MaterialTheme.typography.bodySmall)
                    activity.distanceMeters?.let {
                        Text("Dist: ${it.formatDistance()}", style = MaterialTheme.typography.bodySmall)
                    }
                }
                activity.caloriesBurned?.let {
                     Text("Kcal: ${it.toInt()}", style = MaterialTheme.typography.bodySmall)
                }
            }
            // TODO: Implement Delete Icon Button if needed
            // IconButton(onClick = onDelete, modifier = Modifier.align(Alignment.Top)) {
            //     Icon(Icons.Filled.Delete, contentDescription = "Delete Activity", tint = MaterialTheme.colorScheme.error)
            // }
        }
    }
}
