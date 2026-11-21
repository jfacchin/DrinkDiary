package com.example.drinkdiary.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// runs on an emulator: saves, reads, updates and deletes a drink in a throwaway database
@RunWith(AndroidJUnit4::class)
class DrinkDaoTest {

    private lateinit var db: DrinkDatabase
    private lateinit var dao: DrinkDao

    @Before
    fun setUp() {
        // in-memory database, so the test never touches the real app data
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, DrinkDatabase::class.java).build()
        dao = db.drinkDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun saveReadUpdateDelete() = runBlocking {
        val drink = Drink(
            name = "Iced Matcha Latte",
            type = "Matcha",
            cafe = "Test Cafe",
            price = 6.5,
            rating = 5,
            wouldOrderAgain = true,
            allergens = listOf("Dairy")
        )

        // save it and read it back by id
        val id = dao.insert(drink)
        val saved = dao.getById(id).first()!!
        assertEquals("Iced Matcha Latte", saved.name)
        assertEquals(listOf("Dairy"), saved.allergens)
        assertTrue(dao.getAll().first().any { it.id == id })

        // change the rating
        dao.update(saved.copy(rating = 3))
        assertEquals(3, dao.getById(id).first()!!.rating)

        // delete it
        dao.delete(saved)
        assertNull(dao.getById(id).first())
    }
}
