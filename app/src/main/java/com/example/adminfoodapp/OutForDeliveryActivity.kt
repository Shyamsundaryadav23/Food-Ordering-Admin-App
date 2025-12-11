package com.example.adminfoodapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.adapter.DeliveryAdapter
import com.example.adminfoodapp.databinding.ActivityOutForDeliveryBinding
import com.example.adminfoodapp.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    private lateinit var database: FirebaseDatabase
    private  var listOfCompletedOrderList: ArrayList<OrderDetails> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener { finish() }
        // retrieve and display completed orders
        retrieveCompletedOrderDetail()



        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun retrieveCompletedOrderDetail() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        // Reference to the "CompletedOrder" node in Realtime Database
        val completedOrderReference = database.reference.child("CompletedOrder").orderByChild("currentTime")
        completedOrderReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the list before populating it with new data
                listOfCompletedOrderList.clear()
                for (orderSnapshot in snapshot.children) {
                    val completedOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completedOrder?.let {
                        listOfCompletedOrderList.add(it)
                    }
                }
                // Reverse the list to display the most recent orders first
                listOfCompletedOrderList.reverse()
                setDataIntoRecyclerView()

            }

            private fun setDataIntoRecyclerView() {
                // Initialization list to hold customers name and payment status
                val customerName = mutableListOf<String>()
                val moneyStatus = mutableListOf<Boolean>()

                for(order in listOfCompletedOrderList) {
                    order.userName?.let { customerName.add(it) }
                    order.paymentReceived?.let { moneyStatus.add(it) }
                }
                val adapter = DeliveryAdapter(customerName,moneyStatus)
                binding.deliveryRecyclerView.adapter = adapter
                binding.deliveryRecyclerView.layoutManager = LinearLayoutManager(this@OutForDeliveryActivity)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}