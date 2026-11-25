package com.example.drinkdiary

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
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts


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

        var selectedPhotoUri: Uri? = null
        val drinkPhoto = findViewById<ImageView>(R.id.imageDrinkPhoto)
        val uploadPhotoBtn = findViewById<Button>(R.id.uploadPhoto)
        val drink_NameInput = findViewById<EditText>(R.id.editTextDrinkName)
        val cafe_Name = findViewById<EditText>(R.id.editTextCafe)
        val price_Input = findViewById<EditText>(R.id.editTextPrice)
        val save_Button = findViewById<Button>(R.id.buttonSaveDrink)
        val notes_Input = findViewById<EditText>(R.id.editTextNotes)
        val allergen_Group = findViewById<ChipGroup>(R.id.allergenGroup)
        val drink_rating = findViewById<EditText>(R.id.editTextRating)
        val switchSelection = findViewById<Switch>(R.id.woaSwitch)

    //populating chips from ALLERGENS via data layer
        Drink.ALLERGENS.forEach { allergen ->
            val chip = Chip(this)
            chip.text = allergen
            chip.isCheckable = true
            chip.id = View.generateViewId()
            allergen_Group.addView(chip)
        }

        //creating activity result launcher for the uploaded photos
        val choosePhoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()
        ){uri ->
            if (uri != null){
                selectedPhotoUri = uri

                drinkPhoto.setImageURI(uri)
                uploadPhotoBtn.text = "Change Photo?"
            }
        }
//on click listener for the uploas photo button, this is not where the image is displayed, just the picker
        uploadPhotoBtn.setOnClickListener {
            choosePhoto.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )

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
            selectionsIntent.putExtra("photo_uri", selectedPhotoUri?.toString())
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
