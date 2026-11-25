package com.example.drinkdiary

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.drinkdiary.data.Drink
import com.example.drinkdiary.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import android.content.Intent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // shared with the list and detail fragments
    private val viewModel: DrinkViewModel by viewModels()

    // opens the add drink form and gets the new drink back when the user taps save
    private val addDrinkLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            // user went back without saving
            if (result.resultCode != RESULT_OK || data == null) return@registerForActivityResult

            // these keys have to match the ones AddDrinkActivity puts in the intent
            val drink = Drink(
                photoUri = data.getStringExtra("photo_uri"),
                name = data.getStringExtra("drink_NameInput").orEmpty(),
                type = data.getStringExtra("drink_typeSpinner").orEmpty(),
                cafe = data.getStringExtra("cafe_name").orEmpty(),
                price = data.getDoubleExtra("price_Input", 0.0),
                rating = data.getIntExtra("drink_rating", 0),
                wouldOrderAgain = data.getBooleanExtra("switchSelection", false),
                notes = data.getStringExtra("notes_Input").orEmpty(),
                allergens = data.getStringArrayListExtra("allergen_Group") ?: emptyList()
            )

            // save it to the database, then show it to the user
            viewModel.addDrink(drink) { newId ->
                Snackbar.make(binding.root, "Saved ${drink.name}!", Snackbar.LENGTH_SHORT).show()
                // skip opening details if the app went to the background mid-save, it's still in the list
                if (!supportFragmentManager.isStateSaved) showDrinkDetails(newId)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addDrinkButton.setOnClickListener {
            val intent = Intent(this, AddDrinkActivity::class.java)
            addDrinkLauncher.launch(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            showFragment(DiaryListFragment())
        }

        binding.btnDiaryList.setOnClickListener {
            showFragment(DiaryListFragment())
        }

        binding.btnDrinkDetail.setOnClickListener {
            showFragment(DrinkDetailFragment())
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // opens the detail screen for the drink that was just saved, back returns to the list
    private fun showDrinkDetails(drinkId: Long) {
        val detailFragment = DrinkDetailFragment()
        // same "drinkId" key the list uses when you tap a card
        detailFragment.arguments = Bundle().apply { putLong("drinkId", drinkId) }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}
