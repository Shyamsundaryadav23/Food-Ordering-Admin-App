package com.example.adminfoodapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodapp.databinding.ActivityAdminProfileBinding
import com.example.adminfoodapp.model.userModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private val binding : ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }
    private lateinit var auth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("users")

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.saveinformationbutton.setOnClickListener {
            updateUserData()
        }

        binding.nameEditText.isEnabled = false
        binding.addressEditText.isEnabled = false
        binding.emailEditText.isEnabled = false
        binding.phoneEditText.isEnabled = false
        binding.passwordEditText.isEnabled = false
        binding.saveinformationbutton.isEnabled = false


        var isEnable = false
        binding.editButton.setOnClickListener {
            isEnable = !isEnable
            binding.nameEditText.isEnabled = !isEnable
            binding.addressEditText.isEnabled = !isEnable
            binding.emailEditText.isEnabled = !isEnable
            binding.phoneEditText.isEnabled = !isEnable
            binding.passwordEditText.isEnabled = !isEnable

            if (isEnable)
            {
                binding.nameEditText.requestFocus()
            }
            binding.saveinformationbutton.isEnabled = isEnable
        }
        retrieveUserData()
    }
    private fun retrieveUserData() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val ownerName = snapshot.child("name").getValue()
                        val ownerAddress = snapshot.child("address").getValue()
                        val ownerEmail = snapshot.child("email").getValue()
                        val ownerPhone = snapshot.child("phone").getValue()
                        val ownerPassword = snapshot.child("password").getValue()
                        setDataToTextView(
                            ownerName,
                            ownerAddress,
                            ownerEmail,
                            ownerPhone,
                            ownerPassword
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    private fun setDataToTextView(
        ownerName: Any?,
        ownerAddress: Any?,
        ownerEmail: Any?,
        ownerPhone: Any?,
        ownerPassword: Any?
    ) {
        binding.nameEditText.setText(ownerName.toString())
        binding.addressEditText.setText(ownerAddress.toString())
        binding.emailEditText.setText(ownerEmail.toString())
        binding.phoneEditText.setText(ownerPhone.toString())
        binding.passwordEditText.setText(ownerPassword.toString())
    }
    private fun updateUserData() {
        var updateName = binding.nameEditText.text.toString()
        var updateAddress = binding.addressEditText.text.toString()
        var updateEmail = binding.emailEditText.text.toString()
        var updatePhone = binding.phoneEditText.text.toString()
        var updatePassword = binding.passwordEditText.text.toString()
        var userData = userModel(updateName,updateAddress,updateEmail,updatePhone,updatePassword)
        adminReference.setValue(userData).addOnSuccessListener {
            Toast.makeText(this,"Profile Updated Successfully",Toast.LENGTH_SHORT).show()
            // Update the user's email and password in Firebase Authentication
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
        }.addOnFailureListener {
            Toast.makeText(this,"Profile Update Fail", Toast.LENGTH_SHORT).show()
        }
    }
}