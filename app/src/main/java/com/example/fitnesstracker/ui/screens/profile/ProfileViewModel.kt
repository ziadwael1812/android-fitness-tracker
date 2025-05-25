package com.example.fitnesstracker.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.repository.FitnessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FitnessRepository
) : ViewModel() {
    // TODO: Implement LiveData/StateFlow for user profile data (UserProfile entity)
    // TODO: Functions to fetch and update user profile
}
