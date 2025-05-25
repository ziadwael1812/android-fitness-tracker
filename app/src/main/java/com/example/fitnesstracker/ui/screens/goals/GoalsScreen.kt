package com.example.fitnesstracker.ui.screens.goals

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditCalendar
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
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavHostController,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val targetSteps by viewModel.editableTargetSteps.collectAsState()
    val targetCalories by viewModel.editableTargetCalories.collectAsState()
    val targetDistance by viewModel.editableTargetDistanceMeters.collectAsState()
    val targetActiveMinutes by viewModel.editableTargetActiveMinutes.collectAsState()

    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.resetSaveStatus()
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar(message = "Goal saved successfully!", duration = SnackbarDuration.Short)
            viewModel.resetSaveStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Daily Goals") },
                actions = {
                    IconButton(onClick = { 
                        showDatePicker(context, uiState.selectedDate) { year, month, day ->
                            val calendar = Calendar.getInstance().apply { set(year, month, day) }
                            viewModel.onDateSelected(calendar.time)
                        }
                    }) {
                        Icon(Icons.Filled.EditCalendar, contentDescription = "Select Date for Goal")
                    }
                }
            )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Goals for: ${uiState.selectedDate.toFormattedDateString()}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (uiState.isLoading && uiState.currentGoal == null) {
                CircularProgressIndicator()
            } else {
                GoalTextField(label = "Target Steps", value = targetSteps, onValueChange = viewModel::onTargetStepsChange)
                GoalTextField(label = "Target Calories (kcal)", value = targetCalories, onValueChange = viewModel::onTargetCaloriesChange)
                GoalTextField(label = "Target Distance (meters)", value = targetDistance, onValueChange = viewModel::onTargetDistanceChange)
                GoalTextField(label = "Target Active Minutes", value = targetActiveMinutes, onValueChange = viewModel::onTargetActiveMinutesChange)

                Button(
                    onClick = { viewModel.saveDailyGoal() },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Filled.Check, contentDescription = "Save Goal", modifier = Modifier.padding(end = 8.dp))
                        Text("Save Goal for ${uiState.selectedDate.toFormattedDateString()}")
                    }
                }
            }
        }
    }
}

@Composable
fun GoalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

fun showDatePicker(
    context: android.content.Context,
    initialDate: Date,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            onDateSelected(selectedYear, selectedMonth, selectedDayOfMonth)
        },
        year, month, day
    ).show()
}
