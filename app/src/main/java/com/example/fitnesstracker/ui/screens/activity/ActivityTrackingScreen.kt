package com.example.fitnesstracker.ui.screens.activity

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*nimport androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.util.formatDistance
import com.example.fitnesstracker.util.formatDuration
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ActivityTrackingScreen(
    navController: NavHostController,
    viewModel: ActivityViewModel = hiltViewModel()
) {
    val trackingStatus by viewModel.trackingStatus.observeAsState(TrackingService.TrackingStatus.NOT_STARTED)
    val durationMillis by viewModel.currentDurationMillis.observeAsState(0L)
    val distanceMeters by viewModel.currentDistanceMeters.observeAsState(0f)
    val pathPoints by viewModel.currentPathPoints.observeAsState(emptyList())
    val activityType by viewModel.activityType.observeAsState("Running")

    val context = LocalContext.current
    var showStopDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    // Permissions for location
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val mapUiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true)) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 10f) // Default to Cairo
    }

    // Update map camera to follow user if tracking and path is not empty
    LaunchedEffect(pathPoints) {
        if (trackingStatus == TrackingService.TrackingStatus.TRACKING && pathPoints.isNotEmpty()) {
            pathPoints.lastOrNull()?.let {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 17f))
            }
        }
    }
    
    // Request permissions when screen is first composed or when tracking is attempted to start
    LaunchedEffect(Unit, trackingStatus) {
        if (trackingStatus == TrackingService.TrackingStatus.NOT_STARTED || trackingStatus == TrackingService.TrackingStatus.PAUSED) {
            if (!locationPermissionsState.allPermissionsGranted) {
                // No explicit request here, will be handled by button if user starts tracking.
                // Could show a pre-emptive rationale if desired.
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Track Activity: $activityType") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Map View
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (locationPermissionsState.allPermissionsGranted) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = mapUiSettings,
                        properties = MapProperties(isMyLocationEnabled = true) // Shows blue dot for current location
                    ) {
                        if (pathPoints.size > 1) {
                            Polyline(
                                points = pathPoints,
                                color = Color.Blue,
                                width = 10f
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Location permission is required to show the map and track your route.")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }) {
                            Text("Grant Permission")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatText(label = "Time", value = durationMillis.formatDuration())
                StatText(label = "Distance", value = distanceMeters.toDouble().formatDistance())
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Activity Type Selector (only if not tracking)
            if (trackingStatus != TrackingService.TrackingStatus.TRACKING && trackingStatus != TrackingService.TrackingStatus.PAUSED) {
                ActivityTypeSelector(activityType, onTypeSelected = { viewModel.setActivityType(it) })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { 
                        if (!locationPermissionsState.allPermissionsGranted) {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        } else {
                            viewModel.toggleTracking() 
                        }
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (trackingStatus == TrackingService.TrackingStatus.TRACKING) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (trackingStatus == TrackingService.TrackingStatus.TRACKING) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (trackingStatus == TrackingService.TrackingStatus.TRACKING) "Pause" else "Start/Resume"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (trackingStatus == TrackingService.TrackingStatus.TRACKING) "Pause" else "Start")
                }

                if (trackingStatus == TrackingService.TrackingStatus.TRACKING || trackingStatus == TrackingService.TrackingStatus.PAUSED) {
                    Button(
                        onClick = { showStopDialog = true },
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = "Stop")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Stop")
                    }
                }
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text("Stop Activity?") },
            text = { Text("Are you sure you want to stop and save this activity?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.stopAndSaveActivity()
                        showStopDialog = false
                        navController.popBackStack() // Go back after saving
                    }
                ) { Text("Stop & Save") }
            },
            dismissButton = {
                Button(onClick = { showStopDialog = false }) { Text("Cancel") }
            }
        )
    }
    
    // Optional: Show rationale if permissions were denied and shouldShowRationale is true
    // This requires more complex state management for shouldShowRationale from permission state
}

@Composable
fun StatText(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = value, style = MaterialTheme.typography.headlineMedium, fontSize = 28.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTypeSelector(selectedType: String, onTypeSelected: (String) -> Unit) {
    val activityTypes = listOf("Running", "Walking", "Cycling", "Hiking", "Other")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {}, 
            readOnly = true,
            label = { Text("Activity Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            activityTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}
