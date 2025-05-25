package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Common icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.ui.navigation.Screen // For navigating to Profile if it exists
import com.example.fitnesstracker.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // val themePreference by viewModel.themePreference.collectAsState() // Example

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Example: Profile Setting
        SettingsItem(
            icon = Icons.Filled.AccountCircle,
            title = stringResource(R.string.settings_profile_title),
            subtitle = stringResource(R.string.settings_profile_subtitle),
            onClick = { navController.navigate(Screen.Profile.route) } // Navigate to a dedicated Profile screen
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Example: Theme Setting (using a simple dropdown for now)
        // ThemeSettingItem(currentTheme = themePreference, onThemeChange = { viewModel.setThemePreference(it) })
        // HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SettingsItem(
            icon = Icons.Filled.Palette,
            title = stringResource(R.string.settings_theme_title),
            subtitle = stringResource(R.string.settings_theme_subtitle_placeholder), // Placeholder
            onClick = { /* TODO: Implement theme selection dialog */ }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SettingsItem(
            icon = Icons.Filled.Notifications,
            title = stringResource(R.string.settings_notifications_title),
            subtitle = stringResource(R.string.settings_notifications_subtitle_placeholder),
            onClick = { /* TODO: Implement notification settings screen */ }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        SettingsItem(
            icon = Icons.Filled.Tune, // Or Straighten for units
            title = stringResource(R.string.settings_units_title),
            subtitle = stringResource(R.string.settings_units_subtitle_placeholder), // e.g. Metric (kg, km)
            onClick = { /* TODO: Implement units selection */ }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SettingsItem(
            icon = Icons.Filled.Sync,
            title = stringResource(R.string.settings_data_sync_title),
            subtitle = stringResource(R.string.settings_data_sync_subtitle_placeholder), // e.g. Last synced: Never
            onClick = { /* TODO: Implement data sync options or status */ }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        SettingsItem(
            icon = Icons.Filled.Info,
            title = stringResource(R.string.settings_about_title),
            subtitle = stringResource(R.string.settings_about_subtitle_placeholder), // e.g. App version 1.0.0
            onClick = { /* TODO: Show About dialog or screen */ }
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/* Example Theme Setting Item (can be expanded into a dialog)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingItem(currentTheme: SettingsViewModel.ThemeSetting, onThemeChange: (SettingsViewModel.ThemeSetting) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val themeOptions = SettingsViewModel.ThemeSetting.values()

    SettingsItem(
        icon = Icons.Filled.Palette,
        title = "App Theme",
        subtitle = "Current: ${currentTheme.name.lowercase().replaceFirstChar { it.uppercase() }}",
        onClick = { expanded = true }
    )
    
    if(expanded){
         AlertDialog(
            onDismissRequest = { expanded = false },
            title = { Text("Select Theme") },
            text = {
                Column {
                    themeOptions.forEach { theme ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { onThemeChange(theme); expanded = false }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (theme == currentTheme),
                                onClick = { onThemeChange(theme); expanded = false }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(theme.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { expanded = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}*/
