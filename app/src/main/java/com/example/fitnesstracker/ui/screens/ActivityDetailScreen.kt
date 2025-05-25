package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Common icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.local.ActivityRecord
import com.example.fitnesstracker.ui.viewmodel.ActivityDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    navController: NavController,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val activityRecord by viewModel.activityRecord.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_activity_detail)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.navigate_back_description))
                    }
                }
                // Potentially add an Edit action here if activities are editable
            )
        }
    ) {
        paddingValues ->
        activityRecord?.let {
            record ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ActivityDetailHeader(record)
                Spacer(modifier = Modifier.height(16.dp))
                ActivityDetailMetrics(record)
                Spacer(modifier = Modifier.height(16.dp))
                record.notes?.let {
                    notes -> ActivityDetailNotes(notes)
                }
                // Placeholder for map if GPS data was recorded
                // Spacer(modifier = Modifier.height(16.dp))
                // Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray)) {
                //    Text("Map Placeholder", modifier = Modifier.align(Alignment.Center))
                // }
            }
        } ?: run {
            // Show loading state or error if activityRecord is null
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ActivityDetailHeader(record: ActivityRecord) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = getActivityIcon(record.activityType), // Re-use from ActivityHistoryScreen
            contentDescription = record.activityType,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(record.activityType, style = MaterialTheme.typography.headlineMedium)
            Text(
                SimpleDateFormat("EEEE, MMM dd, yyyy 'at' hh:mm a", Locale.getDefault()).format(record.startTime),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActivityDetailMetrics(record: ActivityRecord) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailMetricItem(Icons.Filled.Timer, stringResource(R.string.metric_duration), formatDuration(record.durationMillis)) // Re-use from ActivityHistoryScreen
            record.distanceKm?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailMetricItem(Icons.Filled.Route, stringResource(R.string.metric_distance), "${String.format("%.2f", it)} km")
            }
            record.caloriesBurned?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailMetricItem(Icons.Filled.LocalFireDepartment, stringResource(R.string.metric_calories), "$it kcal")
            }
            record.steps?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailMetricItem(Icons.Filled.DirectionsRun, stringResource(R.string.metric_steps), "$it steps")
            }
            record.avgHeartRate?.let {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                DetailMetricItem(Icons.Filled.Favorite, stringResource(R.string.metric_avg_heart_rate), "$it bpm")
            }
        }
    }
}

@Composable
fun DetailMetricItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ActivityDetailNotes(notes: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stringResource(R.string.notes_section_title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            Text(notes, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Re-use formatDuration and getActivityIcon or move to a common util file
// private fun formatDuration(millis: Long): String { ... }
// private fun getActivityIcon(activityType: String): ImageVector { ... }
