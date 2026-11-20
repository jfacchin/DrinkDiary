package com.example.drinkdiary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.drinkdiary.data.Drink
import com.example.drinkdiary.data.DrinkDatabase
import com.example.drinkdiary.data.DrinkRepository
import kotlinx.coroutines.launch

// what the screens use to read and save drinks. it survives screen rotation
class DrinkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DrinkRepository(DrinkDatabase.getDatabase(application).drinkDao())

    // the diary list observes this and refreshes by itself when drinks change
    val allDrinks: LiveData<List<Drink>> = repository.allDrinks.asLiveData()

    // the detail screen observes this with the drink id it was given
    fun getDrink(id: Long): LiveData<Drink?> = repository.getDrink(id).asLiveData()

    // saves in the background, then hands back the new id on the main thread
    fun addDrink(drink: Drink, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insert(drink)
            onSaved(id)
        }
    }

    fun updateDrink(drink: Drink) {
        viewModelScope.launch { repository.update(drink) }
    }

    fun deleteDrink(drink: Drink) {
        viewModelScope.launch { repository.delete(drink) }
    }
}
