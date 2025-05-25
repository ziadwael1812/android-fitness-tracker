package com.example.fitnesstracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.local.typeconverters.DateConverter // Corrected import path

@Database(
    entities = [ActivityRecord::class, UserGoal::class],
    version = 1,
    exportSchema = false // Set to true if you want to export schema for migrations
)
@TypeConverters(DateConverter::class)
abstract class FitnessDatabase : RoomDatabase() {

    abstract fun activityRecordDao(): ActivityRecordDao
    abstract fun userGoalDao(): UserGoalDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessDatabase? = null

        fun getDatabase(context: Context): FitnessDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessDatabase::class.java,
                    "fitness_database"
                )
                // Add migrations here if needed for future schema changes
                // .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // Not recommended for production, use proper migrations
                .build()
                INSTANCE = instance
                instance
            }
        }

        // Example Migration (if you increment version and change schema)
        // val MIGRATION_1_2 = object : Migration(1, 2) {
        //     override fun migrate(database: SupportSQLiteDatabase) {
        //         database.execSQL("ALTER TABLE activity_records ADD COLUMN new_column TEXT")
        //     }
        // }
    }
}
