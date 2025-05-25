package com.example.fitnesstracker.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.ui.viewmodel.LogActivityViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogActivityScreen(
    navController: NavController,
    viewModel: LogActivityViewModel = hiltViewModel()
) {
    val formState = viewModel.formState
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = stringResource(R.string.log_activity_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Activity Type Dropdown
        ActivityTypeDropdown(formState.activityType, viewModel.activityTypes, viewModel::onActivityTypeChange, formState.activityTypeError)

        // Date Picker
        DatePickerField(formState.date, viewModel::onDateChange)

        // Time Picker
        TimePickerField(formState.startTimeHours, formState.startTimeMinutes, viewModel::onStartTimeChange)

        // Duration Fields
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = formState.durationHours,
                onValueChange = viewModel::onDurationHoursChange,
                label = { Text(stringResource(R.string.duration_hours_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                isError = formState.durationError != null,
                singleLine = true
            )
            OutlinedTextField(
                value = formState.durationMinutes,
                onValueChange = viewModel::onDurationMinutesChange,
                label = { Text(stringResource(R.string.duration_minutes_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                isError = formState.durationError != null,
                singleLine = true
            )
        }
        formState.durationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Distance Field
        OutlinedTextField(
            value = formState.distanceKm,
            onValueChange = viewModel::onDistanceChange,
            label = { Text(stringResource(R.string.distance_km_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            isError = formState.distanceError != null,
            singleLine = true,
            supportingText = { formState.distanceError?.let { Text(it) } }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Calories Field
        OutlinedTextField(
            value = formState.caloriesBurned,
            onValueChange = viewModel::onCaloriesChange,
            label = { Text(stringResource(R.string.calories_burned_label)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            isError = formState.caloriesError != null,
            singleLine = true,
            supportingText = { formState.caloriesError?.let { Text(it) } }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Steps Field
        OutlinedTextField(
            value = formState.steps,
            onValueChange = viewModel::onStepsChange,
            label = { Text(stringResource(R.string.steps_label_optional)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            isError = formState.stepsError != null,
            singleLine = true,
            supportingText = { formState.stepsError?.let { Text(it) } }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Notes Field
        OutlinedTextField(
            value = formState.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text(stringResource(R.string.notes_label_optional)) },
            modifier = Modifier.fillMaxWidth().height(100.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.saveActivity(
                    onSuccess = {
                        Toast.makeText(context, context.getString(R.string.activity_logged_success), Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = { errorMsg ->
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_activity_button))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTypeDropdown(
    selectedType: String,
    types: List<String>,
    onTypeSelected: (String) -> Unit,
    error: String?
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {}, // Not directly editable
            readOnly = true,
            label = { Text(stringResource(R.string.activity_type_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            isError = error != null
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach {
                type ->
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
    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun DatePickerField(selectedDate: Date, onDateSelected: (Date) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance().apply { time = selectedDate }
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val newDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDayOfMonth)
            }.time
            onDateSelected(newDate)
        },
        year, month, day
    )
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis() // Cannot log future activities

    OutlinedTextField(
        value = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(selectedDate),
        onValueChange = { },
        readOnly = true,
        label = { Text(stringResource(R.string.date_label)) },
        trailingIcon = { Icon(Icons.Filled.CalendarToday, "Select Date") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun TimePickerField(selectedHour: Int, selectedMinute: Int, onTimeSelected: (Int, Int) -> Unit) {
    val context = LocalContext.current
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(hourOfDay, minute)
        },
        selectedHour, selectedMinute, false // false for 12-hour format with AM/PM, true for 24-hour
    )

    OutlinedTextField(
        value = String.format(Locale.getDefault(), "%02d:%02d %s", 
            if (selectedHour == 0 || selectedHour == 12) 12 else selectedHour % 12, 
            selectedMinute, 
            if (selectedHour < 12) "AM" else "PM"),
        onValueChange = { },
        readOnly = true,
        label = { Text(stringResource(R.string.start_time_label)) },
        trailingIcon = { Icon(Icons.Filled.Schedule, "Select Time") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { timePickerDialog.show() }
    )
    Spacer(modifier = Modifier.height(8.dp))
}
