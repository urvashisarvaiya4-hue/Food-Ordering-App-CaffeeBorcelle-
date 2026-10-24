package com.example.caffeeborcelle.AdminAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.model.OrderEntity
import com.google.android.material.button.MaterialButton

class OrderAdapter (
    private val orders: MutableList<OrderEntity>,
    private val layoutRes: Int,
    private val onStatusChange: (OrderEntity, String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView? = itemView.findViewById(R.id.tvOrderId)
        val tvCustomerName: TextView? = itemView.findViewById(R.id.tvCustomerName)
        val tvCustomerEmail: TextView? = itemView.findViewById(R.id.tvCustomerEmail)
        val tvProductPrice: TextView? = itemView.findViewById(R.id.tvProductPrice)
        val tvProducts: TextView? = itemView.findViewById(R.id.tvProducts)
        val tvOrderDate: TextView? = itemView.findViewById(R.id.tvOrderDate)
        val dropdownStatus: AutoCompleteTextView? =
            itemView.findViewById(R.id.spinnerStatus)
        val btnSaveStatus: MaterialButton? =
            itemView.findViewById(R.id.btnSaveStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutRes, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        holder.tvOrderId?.text = "Order ID: ${order.orderId}"
        holder.tvCustomerName?.text = order.fullName
        holder.tvCustomerEmail?.text = order.email
        holder.tvProducts?.text = "Coffee Items: ${order.productNames}"
        holder.tvProductPrice?.text = "₹${order.totalPrice}"
        holder.tvOrderDate?.text = "Date: ${order.orderDate}"

        // 🔥 Status dropdown
        holder.dropdownStatus?.let { dropdown ->
            val statusOptions = holder.itemView.resources
                .getStringArray(R.array.order_status_options)

            val adapterDropdown = ArrayAdapter(
                holder.itemView.context,
                android.R.layout.simple_dropdown_item_1line,
                statusOptions
            )
            dropdown.setAdapter(adapterDropdown)
            dropdown.setText(order.orderStatus, false)
        }

        holder.btnSaveStatus?.setOnClickListener {
            val newStatus = holder.dropdownStatus?.text.toString()
            if (newStatus.isNotEmpty() && newStatus != order.orderStatus) {
                onStatusChange(order, newStatus)
            }
        }
    }

    override fun getItemCount(): Int = orders.size

    fun updateList(newOrders: List<OrderEntity>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}