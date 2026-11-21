package com.example.drinkdiary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// the database queries for drinks. room writes the actual code for these
@Dao
interface DrinkDao {

    // all drinks, newest first. updates by itself whenever the table changes
    @Query("SELECT * FROM drinks ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Drink>>

    // one drink by id, null if it was deleted
    @Query("SELECT * FROM drinks WHERE id = :id")
    fun getById(id: Long): Flow<Drink?>

    // saves a new drink and returns its new id
    @Insert
    suspend fun insert(drink: Drink): Long

    @Update
    suspend fun update(drink: Drink)

    @Delete
    suspend fun delete(drink: Drink)
}
