package com.example.adminfoodapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.adamenuListandroid.view.LayoutInflater.MenuItemAdapter
import com.example.adminfoodapp.databinding.ActivityAllItemBinding
import com.example.adminfoodapp.model.AllMenuModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllItemActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenuModel> = ArrayList()

    private val binding : ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        databaseReference = FirebaseDatabase.getInstance().reference
        retrieveMenuItem()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retrieveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef : DatabaseReference = database.reference.child("Menu")

        // Fetch data from Firebase
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                menuItems.clear()
                for (foodSnapshot in dataSnapshot.children) {
                    val menuItem = foodSnapshot.getValue(AllMenuModel::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", error.message)
            }
        })
    }
    private fun setAdapter() {

        val adapter = MenuItemAdapter(this@AllItemActivity, menuItems, databaseReference){ position ->
            deleteMenuItem(position)
        }
        binding.MenuRecyclerView.layoutManager = LinearLayoutManager(this@AllItemActivity)
        binding.MenuRecyclerView.adapter = adapter
    }

    private fun deleteMenuItem(position: Int) {
        val menuItemToDelete = menuItems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuReference = database.reference.child("Menu").child(menuItemKey!!)
        foodMenuReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                menuItems.removeAt(position)
                binding.MenuRecyclerView.adapter?.notifyItemRemoved(position)
            }
            else
            {
                Toast.makeText(this,"Item Not Deleted",Toast.LENGTH_SHORT).show()
            }
        }
    }
}