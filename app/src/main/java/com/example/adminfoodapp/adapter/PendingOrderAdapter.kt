package com.example.adminfoodapp.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodapp.databinding.PendingOrdersItemBinding

class PendingOrderAdapter(
    private val context: Context,
    private val customerNames: MutableList<String>,
    private val quantity: MutableList<String>,
    private val foodImage: MutableList<String>,
    private val itemClicked: OnItemClicked
    ): RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {
    interface OnItemClicked {
    fun onItemClickListener(position: Int)
    fun onItemAcceptClickListener(position: Int)
    fun onItemDispatchClickListener(position: Int)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingOrderViewHolder {
       val binding = PendingOrdersItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PendingOrderViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerNames.size

    inner class PendingOrderViewHolder(private val binding: PendingOrdersItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                customerNameTextView.text = customerNames[position]
                orderQuantity.text = quantity[position]
                val base64String = foodImage[position]
                val cleanBase64 = base64String.substringAfter(",")

                try {
                    // Decode Base64 string into byte array
                    val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

                    // Convert byte array into Bitmap
                    val decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    // Set the Bitmap to your ImageView
                    pendingOrderFoodItemImage.setImageBitmap(decodedBitmap)

                } catch (e: Exception) {
                    e.printStackTrace()
                    // Optional: fallback placeholder if decoding fails
                    pendingOrderFoodItemImage.setImageResource(android.R.color.darker_gray)
                }

                acceptOrderButton.apply {
                    if (!isAccepted) {
                        text = "Accept"
                    } else {
                        text = "Dispatch"
                    }
                    setOnClickListener {
                        if (!isAccepted) {
                            text = "Dispatch"
                            isAccepted = true
                            showtoast("Order Accepted")
                            itemClicked.onItemAcceptClickListener(position)
                        } else {
                            // Use bindingAdapterPosition here
                            val currentPosition = bindingAdapterPosition
                            if (currentPosition != RecyclerView.NO_POSITION) {
                                customerNames.removeAt(currentPosition)
                                // Also remove from other lists if they should stay in sync
                                quantity.removeAt(currentPosition)
                                foodImage.removeAt(currentPosition)
                                notifyItemRemoved(currentPosition)
                                showtoast("Order Dispatched")
                                itemClicked.onItemDispatchClickListener(position)
                            }
                        }
                    }
                }
                itemView.setOnClickListener {
                    itemClicked.onItemClickListener(position)
                }
            }
        }
      private  fun showtoast(message : String)
        {
            Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
        }
    }
}