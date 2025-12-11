package com.example.adminfoodapp

import android.R.attr.text
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.component3
import androidx.core.graphics.component4
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodapp.databinding.ActivityMainBinding
import com.example.adminfoodapp.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var database : FirebaseDatabase
    private lateinit var auth : FirebaseAuth
    private lateinit var completedOrderReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.addMenu.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenu.setOnClickListener {
            val intent = Intent(this, AllItemActivity::class.java)
            startActivity(intent)
        }
        binding.outForDeliveryButton.setOnClickListener {
            val intent = Intent(this, OutForDeliveryActivity::class.java)
            startActivity(intent)
        }
        //binding.completedOrderButton.setOnClickListener {
        //    val intent = Intent(this, CompletedOrderActivity::class.java)
         //   startActivity(intent)
        //}
        binding.profileCardView.setOnClickListener {
            val intent = Intent(this, AdminProfileActivity::class.java)
            startActivity(intent)

        }
        binding.createNewUserCardView.setOnClickListener {
            val intent = Intent(this, CreateUserActivity::class.java)
            startActivity(intent)
        }
        binding.textView7.setOnClickListener {
            val intent = Intent(this, PendingOrderActivity::class.java)
            startActivity(intent)
        }
        binding.logOutButton.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        pendingOrder()
        completedOrder()
        wholeTimeEarning()

    }

    private fun wholeTimeEarning() {
        var listOfTotalPay = mutableListOf<Int>()
        completedOrderReference = database.reference.child("CompletedOrder")
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    var completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.replace("$","")?.toIntOrNull()
                        ?.let { i ->
                            listOfTotalPay.add(i)
                        }
                }
                binding.wholeTimeEarning.text = listOfTotalPay.sum().toString() + "$"
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun completedOrder() {
        val completedOrderReference = database.reference.child("CompletedOrder")
        var completedOrderItemCount = 0
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrderItemCount = snapshot.childrenCount.toInt()
                binding.completedOrderTextView.text = completedOrderItemCount.toString()

            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun pendingOrder() {
       database = FirebaseDatabase.getInstance()
        val pendingOrderReference = database.reference.child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount = snapshot.childrenCount.toInt()
                binding.pendingOrderTextView.text = pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}