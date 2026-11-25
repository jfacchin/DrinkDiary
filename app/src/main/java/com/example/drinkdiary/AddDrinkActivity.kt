package com.example.drinkdiary

import android.widget.TextView
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Spinner
import android.widget.Button
import android.widget.EditText
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.Chip
import android.widget.Toast
import com.example.drinkdiary.data.Drink
import android.widget.ArrayAdapter
import android.content.Intent
import android.widget.Switch


class AddDrinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_drink)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val drink_typeSpinner = findViewById<Spinner>(R.id.drink_type)
        //populating the spinner selections via TYPE from data layer
        val drinkTypeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Drink.TYPES
        )
        drinkTypeAdapter.setDropDownViewResource(
           android.R.layout.simple_spinner_dropdown_item
        )
        drink_typeSpinner.adapter = drinkTypeAdapter

        val drink_NameInput = findViewById<EditText>(R.id.editTextDrinkName)
        val cafe_Name = findViewById<EditText>(R.id.editTextCafe)
        val price_Input = findViewById<EditText>(R.id.editTextPrice)
        val save_Button = findViewById<Button>(R.id.buttonSaveDrink)
        val cancel_Button = findViewById<Button>(R.id.buttonCancel)
        val notes_Input = findViewById<EditText>(R.id.editTextNotes)
        val allergen_Group = findViewById<ChipGroup>(R.id.allergenGroup)
        val drink_rating = findViewById<EditText>(R.id.editTextRating)
        val switchSelection = findViewById<Switch>(R.id.woaSwitch)
        val editMode = intent.getBooleanExtra("editMode", false)

//populating chips from ALLERGENS via data layer
        Drink.ALLERGENS.forEach { allergen ->
            val chip = Chip(this)
            chip.text = allergen
            chip.isCheckable = true
            chip.id = View.generateViewId()
            allergen_Group.addView(chip)
        }

        // Populate existing drink information when editing
        if (editMode && savedInstanceState == null) {
            drink_NameInput.setText(intent.getStringExtra("drink_NameInput"))
            cafe_Name.setText(intent.getStringExtra("cafe_name"))
            price_Input.setText(
                intent.getDoubleExtra("price_Input", 0.0).toString()
            )
            drink_rating.setText(
                intent.getIntExtra("drink_rating", 0).toString()
            )
            notes_Input.setText(intent.getStringExtra("notes_Input"))

            switchSelection.isChecked =
                intent.getBooleanExtra("switchSelection", false)

            val savedType = intent.getStringExtra("drink_typeSpinner")
            val typePosition = Drink.TYPES.indexOf(savedType)

            if (typePosition >= 0) {
                drink_typeSpinner.setSelection(typePosition)
            }

            val savedAllergens =
                intent.getStringArrayListExtra("allergen_Group")
                    ?: arrayListOf<String>()

            for (i in 0 until allergen_Group.childCount) {
                val chip = allergen_Group.getChildAt(i) as Chip
                chip.isChecked = chip.text.toString() in savedAllergens
            }
        }

        if (editMode) {
            findViewById<TextView>(R.id.formTitle).text = "Edit Drink"
            save_Button.text = "Save Changes"
        }

        // Cancel the form and return without saving a drink
        cancel_Button.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        save_Button.setOnClickListener {
           //all input checks for add drink form
            if (isSelectionEmpty(drink_NameInput)) {
                drink_NameInput.error = "Please enter a drink name!"
                return@setOnClickListener
            }
            if (isSelectionEmpty(cafe_Name)){
                cafe_Name.error = "Please enter shop name!"
                return@setOnClickListener
            }
            if(isSelectionEmpty(price_Input)){
                price_Input.error = "Please input drink price!"
                return@setOnClickListener
            }

            if(isSelectionEmpty(drink_rating)){
                drink_rating.error = "Please input your rating!"
                return@setOnClickListener
            }

        //collecting all the input from the user to send over to main activity
            val drinkName = drink_NameInput.text.toString().trim()
            val cafeName = cafe_Name.text.toString().trim()
            val price = price_Input.text.toString().toDoubleOrNull()
            val drinkSelection = drink_typeSpinner.selectedItem.toString()
            val rating = drink_rating.text.toString().toIntOrNull()
            val notes = notes_Input.text.toString().trim()
            val selectedAllergens = allergen_Group.checkedChipIds.map { id ->
               findViewById<Chip>(id).text.toString()
           }
            val woa = switchSelection.isChecked
            if (rating !in 1..5 || rating == null){
                drink_rating.error = "Please enter a value 1-5!"
                return@setOnClickListener
            }
            if (price == null || price <= 0) {
                    price_Input.error = "Please enter a valid price!"
                    return@setOnClickListener
                }

            val selectionsIntent = Intent()
            selectionsIntent.putExtra("drink_NameInput",drinkName)
            selectionsIntent.putExtra("cafe_name", cafeName)
            selectionsIntent.putExtra("price_Input", price)
            selectionsIntent.putExtra("drink_typeSpinner",drinkSelection)
            selectionsIntent.putExtra("drink_rating",rating)
            selectionsIntent.putExtra("notes_Input", notes)
            selectionsIntent.putExtra("switchSelection",woa)
            selectionsIntent.putStringArrayListExtra(
                "allergen_Group",
                ArrayList(selectedAllergens)
            )

            setResult(RESULT_OK,selectionsIntent)
            finish()
            }
        }

    }

    //helper function for input checks, ensures that every required field is filled out
    private fun isSelectionEmpty(input: EditText): Boolean {
        return input.text.toString().trim().isEmpty()
    }
