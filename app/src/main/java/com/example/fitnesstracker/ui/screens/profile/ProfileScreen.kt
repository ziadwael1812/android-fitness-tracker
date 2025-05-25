package com.example.fitnesstracker.ui.screens.profile

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.fitnesstracker.util.toFormattedDateString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val name by viewModel.editableName.collectAsState()
    val dobString by viewModel.editableDobString.collectAsState()
    val weight by viewModel.editableWeightKg.collectAsState()
    val height by viewModel.editableHeightCm.collectAsState()
    val gender by viewModel.editableGender.collectAsState()
    val preferredUnits by viewModel.editablePreferredUnits.collectAsState()

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.resetSaveStatus() // Clear error after showing
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(
                message = "Profile saved successfully!",
                duration = SnackbarDuration.Short
            )
            viewModel.resetSaveStatus() // Clear success status after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Profile") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading && uiState.userProfile == null) { // Show loader only on initial load
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileTextField(label = "Name", value = name, onValueChange = viewModel::onNameChange)
                DateField(label = "Date of Birth (YYYY-MM-DD)", dateString = dobString, onDateSelected = viewModel::onDobChange)
                ProfileTextField(label = "Weight (kg)", value = weight, onValueChange = viewModel::onWeightChange, keyboardType = KeyboardType.Number)
                ProfileTextField(label = "Height (cm)", value = height, onValueChange = viewModel::onHeightChange, keyboardType = KeyboardType.Number)
                GenderSelector(selectedGender = gender, onGenderSelected = viewModel::onGenderChange)
                UnitSelector(selectedUnit = preferredUnits, onUnitSelected = viewModel::onPreferredUnitsChange)

                Button(
                    onClick = { viewModel.saveUserProfile() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
fun DateField(label: String, dateString: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Try to parse the existing date string to set initial DatePicker values
    try {
        if (dateString.isNotBlank()) {
            calendar.time = dateFormat.parse(dateString)!!
        }
    } catch (e: Exception) { /* Keep default calendar instance */ }

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val newDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDayOfMonth)
            }
            onDateSelected(dateFormat.format(newDate.time))
        },
        year, month, day
    )

    OutlinedTextField(
        value = dateString,
        onValueChange = { /* Read only, changed by dialog */ },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        trailingIcon = {
            Icon(
                Icons.Filled.CalendarToday,
                contentDescription = "Select Date",
                modifier = Modifier.clickable { datePickerDialog.show() }
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelector(selectedGender: String, onGenderSelected: (String) -> Unit) {
    val genders = listOf("Male", "Female", "Other", "Prefer not to say")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {}, // Handled by dropdown selection
            readOnly = true,
            label = { Text("Gender") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth() // Important for anchoring the dropdown menu
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSelector(selectedUnit: String, onUnitSelected: (String) -> Unit) {
    val units = listOf("metric", "imperial")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedUnit.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, // Capitalize for display
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
}
