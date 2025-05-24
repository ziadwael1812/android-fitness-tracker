package com.example.fitnesstracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FitnessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code can go here if needed (e.g., Timber for logging)
    }
}
