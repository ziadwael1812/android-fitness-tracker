# Android Fitness Tracker App

This project is an Android Fitness Tracker application designed to help users monitor their physical activity, set fitness goals, and track their progress.

## 📸 App Preview (Screenshots)

*(Ideally, add 2-4 screenshots of your app here. You'll need to upload them to your repository, perhaps in a `screenshots/` folder, and then link them using markdown: `![Screenshot Alt Text](screenshots/your-image-name.png)`)*

*   ![Dashboard Preview](screenshots/dashboard.png) <!-- Example, replace with your actual image -->
*   ![Workout Log Preview](screenshots/workout_log.png) <!-- Example, replace with your actual image -->
*   ![GPS Tracking Preview](screenshots/gps_tracking.png) <!-- Example, replace with your actual image -->

## ✨ Features

- **Activity Tracking:** Step counting, distance measurement, calorie burn estimation.
- **Workout Logging:** Manual entry for various workout types (running, cycling, gym, etc.).
- **GPS Tracking:** Real-time GPS tracking for outdoor activities like running and cycling.
- **Goal Setting:** Users can set daily or weekly goals for steps, distance, or active minutes.
- **Progress Monitoring:** Visual charts and statistics to show progress over time.
- **User Profile:** Basic user profile to store personal information (height, weight, age) for more accurate calculations.
- **Google Fit Integration:** Syncs data with Google Fit for a holistic view of health data.
- **Data Persistence:** Store user data locally using Room database.
- **Modern UI:** Clean and intuitive user interface built with Jetpack Compose.

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM (Model-View-ViewModel)
- **Asynchronous Programming:** Kotlin Coroutines & Flow
- **Database:** Room Persistence Library
- **Dependency Injection:** Hilt
- **Navigation:** Jetpack Navigation Compose
- **Networking:** Retrofit (for Google Fit API and any other external APIs)
- **Permissions:** Handling for Location, Activity Recognition, etc.
- **Google Services:** Google Fit API

## 📂 Project Structure (Conceptual)

```
android-fitness-tracker/
├── app/
│   ├── build.gradle
│   ├── proguard-rules.pro
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/fitnesstracker/
│   │   │   │   ├── FitnessApplication.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── di/                  # Dependency Injection (Hilt modules)
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/           # Room DB (DAO, Entities, Database)
│   │   │   │   │   │   ├──FitnessDatabase.kt
│   │   │   │   │   │   ├──ActivityLog.kt (Entity)
│   │   │   │   │   │   ├──UserGoal.kt (Entity)
│   │   │   │   │   │   └──ActivityDao.kt
│   │   │   │   │   ├── remote/          # Google Fit API client / Other APIs
│   │   │   │   │   └── repository/      # Repository pattern implementation
│   │   │   │   │       └──FitnessRepository.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── theme/           # Compose UI Theme (Color, Shape, Typography)
│   │   │   │   │   ├── navigation/      # Navigation graph and routes
│   │   │   │   │   ├── screens/         # Composable screens (Dashboard, ActivityDetail, Settings)
│   │   │   │   │   │   ├── DashboardScreen.kt
│   │   │   │   │   │   ├── LogWorkoutScreen.kt
│   │   │   │   │   │   └── ... (other screens)
│   │   │   │   │   └── viewmodel/       # ViewModels for each screen or shared ViewModels
│   │   │   │   │       └──DashboardViewModel.kt
│   │   │   │   ├── util/                # Utility classes, constants, helpers
│   │   │   │   └── services/            # Background services (e.g., for step counting)
│   │   │       └── TrackingService.kt
│   │   └── res/
│   │       ├── drawable/
│   │       ├── layout/             # (Minimal, mostly for older compatibility if any)
│   │       ├── mipmap-...
│   │       └── values/             # (strings.xml, colors.xml, styles.xml)
│   └── test/                     # Unit tests
│   └── androidTest/              # Instrumented tests
├── build.gradle                # Project-level build file
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── .gitignore
```

## ⚙️ Setup and Running

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/ziadwael1812/android-fitness-tracker.git
    cd android-fitness-tracker
    ```
2.  **Open in Android Studio:** Open the project with the latest stable version of Android Studio.
3.  **Google Fit API Configuration:**
    *   Set up a project in the [Google API Console](https://console.developers.google.com/).
    *   Enable the Fitness API.
    *   Create OAuth 2.0 client IDs for Android and add the SHA-1 fingerprint of your signing certificate.
    *   Place necessary API keys or client IDs in your `local.properties` file (this file should be in your project's root directory and is not version controlled by `.gitignore`). For example:
        ```properties
        GOOGLE_FIT_API_KEY="YOUR_API_KEY"
        GOOGLE_FIT_CLIENT_ID="YOUR_CLIENT_ID"
        ```
4.  **Build the project:** Let Android Studio sync and build the project. Resolve any dependency issues.
5.  **Run the app:** Select an emulator or a physical device and run the app.

## 🔒 Permissions

The app will require the following permissions (among others, depending on features):
- `android.permission.ACCESS_FINE_LOCATION` for GPS tracking.
- `android.permission.ACTIVITY_RECOGNITION` for step counting and activity detection.
- `android.permission.INTERNET` for API calls.

Ensure these are declared in the `AndroidManifest.xml` and handled at runtime according to Android best practices.
