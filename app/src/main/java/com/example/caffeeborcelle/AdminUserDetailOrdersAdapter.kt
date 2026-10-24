package com.example.caffeeborcelle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.ActivityOrderProductStatusBlueprintBinding
import com.example.caffeeborcelle.model.OrderEntity

class AdminUserDetailOrdersAdapter (
    private val items: List<OrderEntity>
) : RecyclerView.Adapter<AdminUserDetailOrdersAdapter.VH>() {

    inner class VH(
        private val binding: ActivityOrderProductStatusBlueprintBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderEntity) {

            // Product Name / Price / Status / Date
            binding.productName.text = item.productNames ?: ""
            binding.productPrice.text = "₹${item.totalPrice ?: 0}"
            binding.productStatus.text = item.orderStatus ?: "Pending"
            binding.productDate.text =
                if (!item.orderDate.isNullOrEmpty()) item.orderDate
                else OrderEntity.getCurrentDate()

            // IMAGE
            val imageString = item.productImageUrl ?: ""
            val firstImageUrl = if (imageString.contains(",")) {
                imageString.split(",").firstOrNull()?.trim().orEmpty()
            } else {
                imageString.trim()
            }

            if (firstImageUrl.isNotEmpty()) {
                Glide.with(binding.root.context)
                    .load(firstImageUrl)
                    .placeholder(R.drawable.account_icon)
                    .error(R.drawable.account_icon)
                    .into(binding.productImage)
            } else {
                binding.productImage.setImageResource(R.drawable.account_icon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ActivityOrderProductStatusBlueprintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}