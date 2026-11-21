package com.example.drinkdiary.data

import kotlinx.coroutines.flow.Flow

// the one place the app gets drink data from. the viewmodel uses this, never the dao directly
class DrinkRepository(private val drinkDao: DrinkDao) {

    val allDrinks: Flow<List<Drink>> = drinkDao.getAll()

    fun getDrink(id: Long): Flow<Drink?> = drinkDao.getById(id)

    suspend fun insert(drink: Drink): Long = drinkDao.insert(drink)

    suspend fun update(drink: Drink) = drinkDao.update(drink)

    suspend fun delete(drink: Drink) = drinkDao.delete(drink)
}
