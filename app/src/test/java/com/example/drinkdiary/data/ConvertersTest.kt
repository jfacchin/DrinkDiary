package com.example.drinkdiary.data

import org.junit.Assert.assertEquals
import org.junit.Test

// checks allergens survive being turned into text and back
class ConvertersTest {

    private val converters = Converters()

    @Test
    fun allergensRoundTrip() {
        val allergens = listOf("Dairy", "Nuts")
        val stored = converters.fromAllergens(allergens)
        assertEquals("Dairy,Nuts", stored)
        assertEquals(allergens, converters.toAllergens(stored))
    }

    @Test
    fun noAllergensStaysEmpty() {
        assertEquals(emptyList<String>(), converters.toAllergens(converters.fromAllergens(emptyList())))
    }
}
