package com.example.fitnesstracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.fitnesstracker.ui.viewmodel.SetGoalViewModel // Reusing the same ViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalScreen(
    navController: NavController,
    viewModel: SetGoalViewModel = hiltViewModel() // ViewModel will load existing goal based on nav arg
) {
    val formState = viewModel.formState
    val context = LocalContext.current

    // Wait for the ViewModel to load the goal data if in edit mode
    if (formState.isEditMode && formState.existingGoalId == null) {
        // Show a loading indicator or an empty screen while the goal data is being fetched
        Box(modifier = Modifier.fillMaxSize()) { /* Loading... */ }
        return // Or return a specific loading composable
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_edit_goal)) },
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
            // Goal Type (display only, not editable in edit mode for simplicity)
            OutlinedTextField(
                value = getGoalDisplayName(formState.goalType, formState.period),
                onValueChange = {}, 
                readOnly = true,
                label = { Text(stringResource(R.string.goal_type_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Visually indicate it's not changeable
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Goal Period Dropdown (can be editable)
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
                            Toast.makeText(context, context.getString(R.string.goal_updated_success), Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // Go back to GoalsScreen
                        },
                        onError = { errorMsg ->
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_goal_button))
            }
        }
    }
}

// Note: GoalPeriodDropdown and DatePickerField are reused from SetGoalScreen.kt (or could be moved to a common components file)
