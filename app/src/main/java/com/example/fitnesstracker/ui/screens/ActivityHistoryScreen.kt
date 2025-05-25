package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Common icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.ui.navigation.Screen
import com.example.fitnesstracker.ui.viewmodel.ActivityHistoryViewModel
import com.example.fitnesstracker.ui.viewmodel.ActivitySortOrder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHistoryScreen(
    navController: NavController,
    viewModel: ActivityHistoryViewModel = hiltViewModel()
) {
    val activities by viewModel.activityHistory.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    var showSortDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { 
            // TopAppBar is handled by MainActivity, but we can add actions here if needed
            // Or if this screen needs a specific TopAppBar different from MainActivity's
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.LogActivity.route) }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.fab_log_activity))
            }
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SortOptionsRow(currentSortOrder = sortOrder, onSortOrderChange = { viewModel.updateSortOrder(it) })

            if (activities.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.history_no_activities))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(activities, key = { it.id }) {
                        activity -> ActivityHistoryItem(activity = activity, navController = navController, onDelete = { viewModel.deleteActivity(it) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun SortOptionsRow(currentSortOrder: ActivitySortOrder, onSortOrderChange: (ActivitySortOrder) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = ActivitySortOrder.values().associateWith { getSortOrderName(it) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.sort_by_button_label, getSortOrderName(currentSortOrder)))
            Icon(Icons.Filled.ArrowDropDown, contentDescription = stringResource(R.string.sort_options_dropdown_description))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f) // Adjust width as needed
        ) {
            sortOptions.forEach { (order, name) ->
                DropdownMenuItem(
                    text = { Text(name) }, 
                    onClick = { 
                        onSortOrderChange(order)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun getSortOrderName(sortOrder: ActivitySortOrder): String {
    return when (sortOrder) {
        ActivitySortOrder.DATE_DESC -> stringResource(R.string.sort_date_desc)
        ActivitySortOrder.DATE_ASC -> stringResource(R.string.sort_date_asc)
        ActivitySortOrder.DURATION_DESC -> stringResource(R.string.sort_duration_desc)
        ActivitySortOrder.DURATION_ASC -> stringResource(R.string.sort_duration_asc)
        ActivitySortOrder.DISTANCE_DESC -> stringResource(R.string.sort_distance_desc)
        ActivitySortOrder.DISTANCE_ASC -> stringResource(R.string.sort_distance_asc)
    }
}


@Composable
fun ActivityHistoryItem(
    activity: ActivityRecord,
    navController: NavController,
    onDelete: (ActivityRecord) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(activity.activityType, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Column {
                Text(SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()).format(activity.startTime))
                Text(stringResource(R.string.history_item_duration, formatDuration(activity.durationMillis)))
                activity.distanceKm?.let { Text(stringResource(R.string.history_item_distance, String.format("%.2f", it))) }
                activity.caloriesBurned?.let { Text(stringResource(R.string.history_item_calories, it)) }
            }
        },
        leadingContent = {
            Icon(
                imageVector = getActivityIcon(activity.activityType),
                contentDescription = activity.activityType,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_activity_description), tint = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier.clickable { navController.navigate(Screen.ActivityDetail.createRoute(activity.id)) }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_activity_title)) },
            text = { Text(stringResource(R.string.dialog_delete_activity_confirm_message)) },
            confirmButton = {
                Button(
                    onClick = { 
                        onDelete(activity)
                        showDeleteDialog = false 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete_button_label))
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel_button_label))
                }
            }
        )
    }
}

fun getActivityIcon(activityType: String): ImageVector {
    // This can be expanded with more specific icons
    return when (activityType.lowercase(Locale.getDefault())) {
        "running" -> Icons.Filled.DirectionsRun
        "walking" -> Icons.Filled.DirectionsWalk
        "cycling" -> Icons.Filled.DirectionsBike
        "swimming" -> Icons.Filled.Pool
        "workout", "gym", "strength training" -> Icons.Filled.FitnessCenter
        else -> Icons.Filled.Exercise
    }
}

// Re-use formatDuration from DashboardScreen or move to a util file
private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    return if (hours > 0) {
        String.format("%dh %02dm", hours, minutes)
    } else {
        String.format("%dm", minutes)
    }
}
