package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.adapter.PendingOrderAdapter
import com.example.adminfoodapp.databinding.ActivityPendingOrderBinding
import com.example.adminfoodapp.model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var  binding: ActivityPendingOrderBinding
    private var listOfName : MutableList<String> = mutableListOf()
    private var listOfTotalPrice : MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder : MutableList<String> = mutableListOf()
    private var listOfOrderItem : ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        // Reference to the "OrderDetails" node in Realtime Database
        databaseOrderDetails = database.reference.child("OrderDetails")

        getOrdersDetails()
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getOrdersDetails() {
        // Fetch data from Firebase
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetail = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetail?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrderItem) {
            // add data to respective list for populating recycler view
            orderItem.userName?.let{ listOfName.add(it) }
            orderItem.totalPrice?.let { listOfTotalPrice.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach { listOfImageFirstFoodOrder.add(it) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PendingOrderAdapter(this,listOfName,listOfTotalPrice,listOfImageFirstFoodOrder, this)
        binding.pendingOrderRecyclerView.adapter = adapter
    }


    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("UserOrderDetails", userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        // Handle Item Acceptance and update database
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let { database.reference.child("OrderDetails").child(it)  }
        clickItemOrderReference?.child("AcceptedOrder")?.setValue(true)
        updateOrderAcceptedStatus(position)
    }
    override fun onItemDispatchClickListener(position: Int) {
        // Handle Item Dispatch and update database
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }

    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsReference = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsReference.removeValue()
            .addOnSuccessListener {
                // Item deleted successfully
                Toast.makeText(this, "Order is Dispatched", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                // Handle failure
                Toast.makeText(this,"Order is not Dispatched",Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptedStatus(position: Int) {
        // Update order acceptance in user's Buy history and OrderDetails
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("OrderHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }

}
