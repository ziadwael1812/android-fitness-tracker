package com.example.fitnesstracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val preferredUnits by viewModel.editablePreferredUnits.collectAsState()
    // val themePreference by viewModel.editableTheme.collectAsState() // Example for another setting

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.resetSaveStatus()
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(message = "Settings saved successfully!", duration = SnackbarDuration.Short)
            viewModel.resetSaveStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("App Settings") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (uiState.isLoading && uiState.userProfile == null) {
                CircularProgressIndicator()
            } else { // Only show settings if profile is loaded or not in initial loading state
                 UnitPreferenceSelector(
                    selectedUnit = preferredUnits,
                    onUnitSelected = viewModel::onPreferredUnitsChange
                )
            }
            

            // Divider()
            // ThemePreferenceSelector(
            //     selectedTheme = themePreference,
            //     onThemeSelected = viewModel::onThemeChange
            // )
            // Add more settings sections here (e.g., Notification Preferences, Data Sync)

            Spacer(modifier = Modifier.weight(1f)) // Push button to bottom

            Button(
                onClick = { viewModel.saveSettings() },
                enabled = !uiState.isLoading, // Disable button if any loading is happening
                modifier = Modifier.fillMaxWidth()
            ) {
                // Show loader on button only if saving settings, not during initial load
                if (uiState.isLoading && uiState.userProfile != null) { 
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Save Settings")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitPreferenceSelector(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit
) {
    val units = listOf("metric", "imperial")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Measurement Units", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedUnit.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Preferred Units") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) },
                        onClick = {
                            onUnitSelected(unit)
                            expanded = false
                        }
                    )
                }
            }
        }
        Text(
            text = "This affects how distance and weight are displayed throughout the app.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/* Example for a Theme Selector - to be implemented properly with DataStore or similar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePreferenceSelector(
    selectedTheme: String,
    onThemeSelected: (String) -> Unit
) {
    val themes = listOf("System Default", "Light", "Dark")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("App Theme", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedTheme,
                onValueChange = {},
                readOnly = true,
                label = { Text("Theme") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                themes.forEach { theme ->
                    DropdownMenuItem(
                        text = { Text(theme) },
                        onClick = {
                            onThemeSelected(theme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
*/
