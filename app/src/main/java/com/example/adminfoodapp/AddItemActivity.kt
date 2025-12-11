package com.example.adminfoodapp

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminfoodapp.databinding.ActivityAddItemBinding
import com.example.adminfoodapp.model.AllMenuModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddItemActivity : AppCompatActivity() {

    // Food Item Details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private var foodImageUri: Uri? = null
    private var foodImageBase64: String? = null

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.AddItemButton.setOnClickListener {
            // get data from edit text
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.ingredients.text.toString().trim()

            if (foodName.isNotBlank() && foodPrice.isNotBlank() &&
                foodDescription.isNotBlank() && foodIngredient.isNotBlank()
            ) {
                if (foodImageBase64 != null) {
                    uploadData()
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadData() {
        // Reference to the "Menu" node in Realtime Database
        val menuRef: DatabaseReference = database.getReference("Menu")

        // Generate unique key
        val newItemKey: String? = menuRef.push().key

        // Check if all fields are provided
        if (foodImageUri != null && foodName.isNotEmpty() && foodPrice.isNotEmpty()) {
            val newItem = AllMenuModel(
                newItemKey,
                foodName,
                foodPrice,
                foodDescription,
                foodIngredient,
                foodImageBase64!!  // Store image URI directly
            )
            // Save to Realtime Database
            newItemKey?.let { key ->
                menuRef.child(key).setValue(newItem)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to add item", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
        }
    }

    // Pick image and convert to Base64
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.selectedImage.setImageURI(uri)
            foodImageUri = uri
            foodImageBase64 = convertImageToBase64(uri)
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun convertImageToBase64(imageUri: Uri): String {
        val inputStream = contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}
