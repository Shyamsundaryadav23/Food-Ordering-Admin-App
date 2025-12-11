package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodapp.databinding.ActivitySignUp2Binding
import com.example.adminfoodapp.model.userModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity2 : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var userName : String
    private lateinit var nameOfResturant : String
    private lateinit var database : DatabaseReference
    private val binding: ActivitySignUp2Binding by lazy {
        ActivitySignUp2Binding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        // Intialize Firebase Auth
        auth = Firebase.auth
        // Initialize Firebase Database
        database = Firebase.database.reference

        binding.createAccountButton.setOnClickListener{
            // Get the user input from the EditText fields
            userName = binding.usernameEditText.text.toString().trim()
            nameOfResturant = binding.editTextResturantName.text.toString().trim()
            email = binding.useremailEditText.text.toString().trim()
            password = binding.userpasswordEditText.text.toString().trim()

            if (userName.isEmpty() || nameOfResturant.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                createAccount(email, password)
            }
        }
        binding.alreadHaveAccountButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        val locationList = arrayOf("Jaipur", "Delhi", "Mumbai", "Kolkata", "Chennai")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, locationList)
        val autoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)
    }
   // Save user data to Firebase Realtime Database
    private fun saveUserData() {
        userName = binding.usernameEditText.text.toString().trim()
        nameOfResturant = binding.editTextResturantName.text.toString().trim()
        email = binding.useremailEditText.text.toString().trim()
        password = binding.userpasswordEditText.text.toString().trim()

        val user = userModel(userName,nameOfResturant,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("users").child(userId).setValue(user)

        }

    private fun createAccount(email: String, password: String): Unit {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else
            {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "createAccount: ${task.exception}")
            }
        }
        return Unit

    }
}