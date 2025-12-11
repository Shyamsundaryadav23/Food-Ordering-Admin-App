package com.example.adminfoodapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.adArray.OrderDetailAdapter
import com.example.adminfoodapp.databinding.ActivityOrderDetailBinding
import com.example.adminfoodapp.model.OrderDetails

class OrderDetailActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailBinding by lazy {
        ActivityOrderDetailBinding.inflate(layoutInflater)
    }
    private var userName: String? = null
    private var userAddress: String? = null
    private var userPhone: String? = null
    private var totalPrice: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val receiveOrderDetails = intent.getSerializableExtra("UserOrderDetails") as OrderDetails
        receiveOrderDetails.let { orderDetails ->
            userName = receiveOrderDetails.userName
            foodNames = receiveOrderDetails.foodNames as ArrayList<String>
            foodImages = receiveOrderDetails.foodImages as ArrayList<String>
            foodQuantity = receiveOrderDetails.foodQuantities as ArrayList<Int>
            foodPrices = receiveOrderDetails.foodPrices as ArrayList<String>
            userAddress = receiveOrderDetails.address
            userPhone = receiveOrderDetails.phoneNumber
            totalPrice = receiveOrderDetails.totalPrice
            setOrderDetail()
            setAdapter()
        }
    }


    private fun setOrderDetail() {
        binding.name.text = userName
        binding.address.text = userAddress
        binding.phone.text = userPhone
        binding.totalPay.text = totalPrice
    }

    private fun setAdapter() {
        binding.orderDetailsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailAdapter(this, foodNames, foodImages, foodQuantity, foodPrices)
        binding.orderDetailsRecyclerView.adapter = adapter
    }
}