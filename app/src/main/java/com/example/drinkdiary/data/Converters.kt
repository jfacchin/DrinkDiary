package com.example.drinkdiary.data

import androidx.room.TypeConverter

// room can't save a list, so allergens are stored as one text value like "Dairy,Nuts"
class Converters {

    @TypeConverter
    fun fromAllergens(allergens: List<String>): String = allergens.joinToString(",")

    @TypeConverter
    fun toAllergens(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split(",")
}
