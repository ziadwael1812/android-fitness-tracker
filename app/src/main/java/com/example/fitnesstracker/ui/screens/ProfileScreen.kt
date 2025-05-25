package com.example.fitnesstracker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.fitnesstracker.ui.viewmodel.Gender
import com.example.fitnesstracker.ui.viewmodel.ProfileViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val formState = viewModel.formState
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_profile)) },
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
            OutlinedTextField(
                value = formState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.profile_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.nameError != null,
                supportingText = { formState.nameError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.age,
                onValueChange = viewModel::onAgeChange,
                label = { Text(stringResource(R.string.profile_age_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.ageError != null,
                supportingText = { formState.ageError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.weightKg,
                onValueChange = viewModel::onWeightChange,
                label = { Text(stringResource(R.string.profile_weight_kg_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.weightError != null,
                supportingText = { formState.weightError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.heightCm,
                onValueChange = viewModel::onHeightChange,
                label = { Text(stringResource(R.string.profile_height_cm_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.heightError != null,
                supportingText = { formState.heightError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            GenderDropdown(formState.gender, viewModel.genderOptions, viewModel::onGenderChange)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.saveProfile(
                        onSuccess = {
                            Toast.makeText(context, context.getString(R.string.profile_saved_success), Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = { errorMsg -> 
                            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_profile_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: Gender,
    genders: Array<Gender>,
    onGenderSelected: (Gender) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedGender.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }.replace("_", " "),
            onValueChange = {}, 
            readOnly = true,
            label = { Text(stringResource(R.string.profile_gender_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            genders.forEach {
                gender ->
                DropdownMenuItem(
                    text = { Text(gender.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercaseChar() }.replace("_", " ")) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}
