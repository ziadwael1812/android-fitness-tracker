package com.example.fitnesstracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Common icons like Add, Edit, Delete
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
import com.example.fitnesstracker.data.local.GoalPeriod
import com.example.fitnesstracker.data.local.GoalType
import com.example.fitnesstracker.data.local.UserGoal
import com.example.fitnesstracker.ui.navigation.Screen
import com.example.fitnesstracker.ui.viewmodel.GoalsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    navController: NavController,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val goals by viewModel.activeGoals.collectAsState()
    var showSetGoalTypeDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showSetGoalTypeDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.fab_set_goal))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (goals.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.goals_no_active_goals))
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(goals, key = { it.id }) {
                        goal -> GoalItem(goal = goal, navController = navController, onDelete = { viewModel.deleteGoal(it) })
                        HorizontalDivider()
                    }
                }
            }
        }

        if (showSetGoalTypeDialog) {
            SelectGoalTypeDialog(
                onDismiss = { showSetGoalTypeDialog = false },
                onGoalTypeSelected = {
                    goalType -> 
                    navController.navigate(Screen.SetGoal.createRoute(goalType.name))
                    showSetGoalTypeDialog = false
                }
            )
        }
    }
}

@Composable
fun GoalItem(
    goal: UserGoal,
    navController: NavController,
    onDelete: (UserGoal) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(getGoalDisplayName(goal.type, goal.period), fontWeight = FontWeight.Bold) },
        supportingContent = {
            Column {
                Text(stringResource(R.string.goals_item_target, goal.targetValue.toInt().toString(), goal.type.name.lowercase(Locale.getDefault())))
                Text(stringResource(R.string.goals_item_set_on, SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(goal.creationDate)))
                goal.appliesToDate?.let {
                    Text(stringResource(R.string.goals_item_applies_to, SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it)))
                }
            }
        },
        leadingContent = {
            Icon(
                imageVector = getGoalIcon(goal.type),
                contentDescription = goal.type.name,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Row {
                IconButton(onClick = { navController.navigate(Screen.EditGoal.createRoute(goal.id)) }) {
                     Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit_goal_description), tint = MaterialTheme.colorScheme.secondary)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_goal_description), tint = MaterialTheme.colorScheme.error)
                }
            }
        },
        modifier = Modifier.clickable { /* Future: Maybe navigate to a goal progress detail screen */ }
    )

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_goal_title)) },
            text = { Text(stringResource(R.string.dialog_delete_goal_confirm_message)) },
            confirmButton = {
                Button(
                    onClick = { 
                        onDelete(goal)
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

@Composable
fun getGoalDisplayName(type: GoalType, period: GoalPeriod): String {
    val periodStr = period.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() }
    val typeStr = when(type) {
        GoalType.STEPS -> stringResource(R.string.goal_type_steps)
        GoalType.DISTANCE_KM -> stringResource(R.string.goal_type_distance)
        GoalType.ACTIVE_MINUTES -> stringResource(R.string.goal_type_active_minutes)
        GoalType.CALORIES_BURNED -> stringResource(R.string.goal_type_calories)
    }
    return "$periodStr $typeStr"
}

@Composable
fun getGoalIcon(type: GoalType): ImageVector {
    return when(type) {
        GoalType.STEPS -> Icons.Filled.DirectionsRun
        GoalType.DISTANCE_KM -> Icons.Filled.Route // Or DirectionsBike, etc.
        GoalType.ACTIVE_MINUTES -> Icons.Filled.Timer
        GoalType.CALORIES_BURNED -> Icons.Filled.LocalFireDepartment
    }
}

@Composable
fun SelectGoalTypeDialog(onDismiss: () -> Unit, onGoalTypeSelected: (GoalType) -> Unit) {
    val goalTypes = GoalType.values()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_select_goal_type_title)) },
        text = {
            Column {
                goalTypes.forEach { goalType ->
                    Text(
                        text = getGoalDisplayName(goalType, GoalPeriod.DAILY), // Display name for selection
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onGoalTypeSelected(goalType) }
                            .padding(vertical = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button_label))
            }
        }
    )
}

