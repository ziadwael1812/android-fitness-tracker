package com.example.fitnesstracker.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.local.GoalPeriod
import com.example.fitnesstracker.data.local.GoalType
import com.example.fitnesstracker.ui.viewmodel.SetGoalViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetGoalScreen(
    navController: NavController,
    viewModel: SetGoalViewModel = hiltViewModel()
) {
    val formState = viewModel.formState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_set_goal)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.navigate_back_description))
                    }
                }
            )
        }
    ) {
        paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Goal Type (display only, as it's passed via nav arg or default)
            OutlinedTextField(
                value = getGoalDisplayName(formState.goalType, formState.period), // Show period as well for context
                onValueChange = {}, 
                readOnly = true,
                label = { Text(stringResource(R.string.goal_type_label)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Goal Period Dropdown
            GoalPeriodDropdown(formState.period, viewModel::onPeriodChange)
            Spacer(modifier = Modifier.height(8.dp))

            // Target Value
            OutlinedTextField(
                value = formState.targetValue,
                onValueChange = viewModel::onTargetValueChange,
                label = { Text(stringResource(R.string.target_value_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                isError = formState.targetValueError != null,
                supportingText = { formState.targetValueError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Applies to Date (only for Daily goals)
            if (formState.period == GoalPeriod.DAILY) {
                DatePickerField(
                    selectedDate = formState.appliesToDate ?: Date(), 
                    onDateSelected = viewModel::onAppliesToDateChange,
                    label = stringResource(R.string.applies_to_date_label)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    viewModel.saveGoal(
                        onSuccess = {
                            Toast.makeText(context, context.getString(R.string.goal_set_success), Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // Go back to GoalsScreen
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_goal_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalPeriodDropdown(
    selectedPeriod: GoalPeriod,
    onPeriodSelected: (GoalPeriod) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val periods = GoalPeriod.values()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedPeriod.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercaseChar() },
            onValueChange = {}, 
            readOnly = true,
            label = { Text(stringResource(R.string.goal_period_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach {
                period ->
                DropdownMenuItem(
                    text = { Text(period.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Reusing DatePickerField from LogActivityScreen, but with a specific label
@Composable
fun DatePickerField(selectedDate: Date, onDateSelected: (Date) -> Unit, label: String) {
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
    // For new goals, don't allow setting a date in the past for appliesToDate if it's a daily goal
    // This logic can be enhanced in ViewModel or here
    // if (label == stringResource(id = R.string.applies_to_date_label)) {
    //    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24) // yesterday
    // }

    OutlinedTextField(
        value = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(selectedDate),
        onValueChange = { },
        readOnly = true,
        label = { Text(label) },
        trailingIcon = { Icon(Icons.Filled.CalendarToday, "Select Date") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }
    )
}
