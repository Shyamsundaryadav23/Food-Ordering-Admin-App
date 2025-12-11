package com.example.adminfoodapp.adArray
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodapp.databinding.OrderDetailsItemBinding

class OrderDetailAdapter(
    private var context: Context,
    private var foodName: ArrayList<String>,
    private var foodImage: ArrayList<String>,
    private var foodQuantitys: ArrayList<Int>,
    private var foodPrice: ArrayList<String>

): RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderDetailViewHolder {
       val binding = OrderDetailsItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return OrderDetailViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderDetailViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodName.size

    inner class OrderDetailViewHolder(private val binding: OrderDetailsItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
               orderFoodName.text = foodName[position]
                foodQuantity.text = foodQuantitys[position].toString()
                orderFoodPrice.text = foodPrice[position]

                val base64String = foodImage[position]
                val cleanBase64 = base64String.substringAfter(",")

                try {
                    // Decode Base64 string into byte array
                    val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                    // Convert byte array into Bitmap
                    val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    // Set the Bitmap to your ImageVi
                orderFoodItemImage.setImageBitmap(decodedBitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Optional: fallback placeholder if decoding fails
                    orderFoodItemImage.setImageResource(android.R.color.darker_gray)
                }

            }

        }
    }
}