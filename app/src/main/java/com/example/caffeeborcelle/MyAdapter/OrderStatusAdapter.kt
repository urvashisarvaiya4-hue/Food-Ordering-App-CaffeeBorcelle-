package com.example.caffeeborcelle.MyAdapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.ActivityOrderProductStatusBlueprintBinding
import com.example.caffeeborcelle.model.OrderEntity

class OrderStatusAdapter (
    private var orders: List<OrderEntity>
) : RecyclerView.Adapter<OrderStatusAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(
        val binding: ActivityOrderProductStatusBlueprintBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {

        val binding = ActivityOrderProductStatusBlueprintBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {

        val order = orders[position]

        holder.binding.productName.text = order.productNames
        holder.binding.productPrice.text = "₹${order.totalPrice}"
        holder.binding.productDate.text = "Ordered on: ${order.orderDate}"
        holder.binding.productStatus.text = order.orderStatus

        // 🔥 Load product image from Firebase / URL
        Glide.with(holder.itemView.context)
            .load(order.productImageUrl)
            .placeholder(R.drawable.broken_image)
            .into(holder.binding.productImage)

        // 🔥 Status color
        val color = when (order.orderStatus.lowercase()) {
            "pending"    -> "#FFC107"
            "dispatched" -> "#2196F3"
            "delivered"  -> "#2E7D32"
            "cancelled"  -> "#F44336"
            "in transit" -> "#9C27B0"
            "returned"   -> "#E91E63"
            "failed"     -> "#795548"
            "processing" -> "#FF5722"
            else -> "#607D8B"
        }

        holder.binding.productStatus.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(color))
    }

    override fun getItemCount(): Int = orders.size

    fun updateList(newOrders: List<OrderEntity>) {
        orders = newOrders
        notifyDataSetChanged()
    }
}