package com.example.drinkdiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// the app's local database. bump the version if the Drink fields change
@Database(entities = [Drink::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DrinkDatabase : RoomDatabase() {

    abstract fun drinkDao(): DrinkDao

    companion object {
        // only one copy of the database for the whole app
        @Volatile
        private var instance: DrinkDatabase? = null

        fun getDatabase(context: Context): DrinkDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DrinkDatabase::class.java,
                    "drink_diary.db"
                ).build().also { instance = it }
            }
    }
}
