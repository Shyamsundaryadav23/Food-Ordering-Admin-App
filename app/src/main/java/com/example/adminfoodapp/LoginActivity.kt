package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.adminfoodapp.databinding.ActivityLoginBinding
import com.example.adminfoodapp.model.userModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {
    private var userName : String ?= null
    private var nameOfResturant : String ?= null
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Database
        database = Firebase.database.reference

        //

        binding.LoginButton.setOnClickListener {
            // Get the user input from the EditText fields
            email = binding.useremailEditText.text.toString().trim()
            password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else {
                createAccount(email, password)
            }
        }
        binding.dontHaveAccountButton.setOnClickListener {
            val intent = Intent(this,SignUpActivity2::class.java)
            startActivity(intent)
        }

    }

    private fun createAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser?= auth.currentUser
                updateUI(user)
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                } else {
                      auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                          if (task.isSuccessful) {
                              val user: FirebaseUser?= auth.currentUser
                              saveUserData()
                              updateUI(user)
                              Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                          }
                          else {
                              Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                              Log.d("Account", "createAccount: ${task.exception}")
                          }
                      }
                }

        }
    }

    private fun saveUserData() {
        email = binding.useremailEditText.text.toString().trim()
        password = binding.editTextPassword.text.toString().trim()
        val user = userModel(email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("users").child(userId).setValue(user)
    }
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null)
            updateUI(currentUser)
    }
    private fun updateUI(user: FirebaseUser?)
    {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

