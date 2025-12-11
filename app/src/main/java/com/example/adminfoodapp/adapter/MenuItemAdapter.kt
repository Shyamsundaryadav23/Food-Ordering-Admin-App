package com.example.adminfoodapp.adamenuListandroid.view.LayoutInflater
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminfoodapp.databinding.ItemItemBinding
import com.example.adminfoodapp.model.AllMenuModel
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: android.content.Context,
    private val menuList: ArrayList<AllMenuModel>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {
    private val itemQuantities = IntArray(menuList.size){1}
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddItemViewHolder {
        val binding = ItemItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AddItemViewHolder,
        position: Int
    ) {
       holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size
    inner class AddItemViewHolder(private val binding: ItemItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                val menuItem = menuList[position]

                foodNameTextView.text =menuItem.foodName
                foodPriceTextView.text = menuItem.foodPrice
                quantityTextView.text = quantity.toString()

                val base64String = menuItem.foodImage
                if (!base64String.isNullOrEmpty()) {
                    try {
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        foodImageView.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                minusButton.setOnClickListener { decreaseQuantity(position) }
                plusButton.setOnClickListener { increaseQuantity(position) }
                deleteButton.setOnClickListener { onDeleteClickListener(position) }
            }
        }

//        private fun deleteItem(position: Int) {
//            menuList.removeAt(position)
//            menuList.removeAt(position)
//            menuList.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, menuList.size)
//        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10)
            {
                itemQuantities[position]++
                binding.quantityTextView.text = itemQuantities[position].toString()
            }

            
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1)
            {
                itemQuantities[position]--
                binding.quantityTextView.text = itemQuantities[position].toString()
            }

        }

    }
}