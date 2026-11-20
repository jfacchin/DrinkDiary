package com.example.drinkdiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// one drink in the diary, saved as a row in the "drinks" table
@Entity(tableName = "drinks")
data class Drink(
    // room picks the id when the drink is saved, so leave it as 0
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    // one of TYPES below
    val type: String,
    val cafe: String,
    val price: Double,
    // 1 to 5 stars
    val rating: Int,
    val wouldOrderAgain: Boolean = false,
    val notes: String = "",
    // where the camera photo is saved, null if there's no photo
    val photoUri: String? = null,
    // cafe location from gps, null if not added
    val latitude: Double? = null,
    val longitude: Double? = null,
    // picked from ALLERGENS below
    val allergens: List<String> = emptyList(),
    // when it was added, used to show the newest drinks first
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        // options for the drink type picker in the add form
        val TYPES = listOf("Coffee", "Matcha", "Tea")

        // options for the allergen chips in the add form
        val ALLERGENS = listOf("Dairy", "Soy", "Nuts", "Gluten", "Eggs")
    }
}
