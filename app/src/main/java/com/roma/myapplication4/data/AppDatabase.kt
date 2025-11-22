package com.roma.myapplication4.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Simple migration object
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Since the changes are mainly in data classes and we use fallback, 
                // we don't need to write complex migration queries here.
                // For a real app, you would alter tables here.
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2) // Add the migration
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
