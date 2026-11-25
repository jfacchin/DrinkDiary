package com.example.drinkdiary

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.drinkdiary.databinding.FragmentDrinkDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrinkDetailFragment : Fragment(R.layout.fragment_drink_detail) {

    private val viewModel: DrinkViewModel by activityViewModels()

    private var selectedDrink: com.example.drinkdiary.data.Drink? = null

    // Receive edited drink information and update the existing record
    private val editDrinkLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            val originalDrink = selectedDrink

            if (result.resultCode != android.app.Activity.RESULT_OK ||
                data == null || originalDrink == null
            ) return@registerForActivityResult

            val updatedDrink = originalDrink.copy(
                name = data.getStringExtra("drink_NameInput").orEmpty(),
                type = data.getStringExtra("drink_typeSpinner").orEmpty(),
                cafe = data.getStringExtra("cafe_name").orEmpty(),
                price = data.getDoubleExtra("price_Input", 0.0),
                rating = data.getIntExtra("drink_rating", 0),
                wouldOrderAgain = data.getBooleanExtra("switchSelection", false),
                notes = data.getStringExtra("notes_Input").orEmpty(),
                allergens = data.getStringArrayListExtra("allergen_Group")
                    ?: emptyList()
            )

            viewModel.updateDrink(updatedDrink)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDrinkDetailBinding.bind(view)

        // Get the selected drink ID
        val drinkId = arguments?.getLong("drinkId") ?: 0L

        // Return to the diary list
        binding.btnBack.setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 0) {
                parentFragmentManager.popBackStack()
            } else {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, DiaryListFragment())
                    .commit()
            }
        }

        if (drinkId == 0L) {
            binding.detailName.text = "Select a drink from your diary"
            return
        }

        // Retrieve the selected drink from the database
        viewModel.getDrink(drinkId).observe(viewLifecycleOwner) { drink ->

            if (drink == null) {
                binding.detailName.text = "Drink not found"
                return@observe
            }

            // Confirm before deleting the selected drink
            binding.buttonDeleteDrink.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Drink")
                    .setMessage("Are you sure you want to delete ${drink.name}?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteDrink(drink)
                        binding.btnBack.performClick()
                    }
                    .show()
            }

            selectedDrink = drink

// Open the form with the existing drink information
            binding.buttonEditDrink.setOnClickListener {
                val intent = Intent(requireContext(), AddDrinkActivity::class.java)

                intent.putExtra("editMode", true)
                intent.putExtra("drink_NameInput", drink.name)
                intent.putExtra("drink_typeSpinner", drink.type)
                intent.putExtra("cafe_name", drink.cafe)
                intent.putExtra("price_Input", drink.price)
                intent.putExtra("drink_rating", drink.rating)
                intent.putExtra("switchSelection", drink.wouldOrderAgain)
                intent.putExtra("notes_Input", drink.notes)

                intent.putStringArrayListExtra(
                    "allergen_Group",
                    ArrayList(drink.allergens)
                )

                editDrinkLauncher.launch(intent)
            }

            binding.detailName.text = drink.name
            binding.detailCafe.text = "Cafe: ${drink.cafe}"
            binding.detailType.text = "Type: ${drink.type}"

            binding.detailPrice.text = String.format(
                Locale.US, "Price: $%.2f", drink.price
            )

            val rating = drink.rating.coerceIn(0, 5)

            binding.detailRating.text =
                "★".repeat(rating) + "☆".repeat(5 - rating)

            binding.detailOrderAgain.text =
                "Would order again: " +
                        if (drink.wouldOrderAgain) "Yes" else "No"

            binding.detailAllergens.text =
                "Allergens: " + if (drink.allergens.isEmpty()) {
                    "None recorded"
                } else {
                    drink.allergens.joinToString(", ")
                }

            binding.detailNotes.text =
                "Notes: ${drink.notes.ifBlank { "No notes added" }}"

            // Display GPS location if available
            binding.detailLocation.text =
                if (drink.latitude != null && drink.longitude != null) {
                    "Location: ${drink.latitude}, ${drink.longitude}"
                } else {
                    "Location: Not added"
                }

            // Display the saved photo if available
            if (!drink.photoUri.isNullOrBlank()) {
                binding.detailPhoto.visibility = View.VISIBLE
                binding.detailPhoto.setImageURI(Uri.parse(drink.photoUri))
            } else {
                binding.detailPhoto.visibility = View.GONE
            }

            // Format the date the drink was added
            val dateFormat = SimpleDateFormat(
                "MMMM dd, yyyy", Locale.US
            )

            binding.detailDate.text =
                "Added: ${dateFormat.format(Date(drink.createdAt))}"
        }
    }
}