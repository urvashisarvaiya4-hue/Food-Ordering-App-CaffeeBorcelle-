package com.example.caffeeborcelle

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caffeeborcelle.AdminAdapter.OrderAdapter
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.ActivityAdminOrdersScreenBinding
import com.example.caffeeborcelle.model.OrderEntity
import com.google.firebase.database.*

class activity_admin_orders_screen : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrdersScreenBinding
    private lateinit var adapter: OrderAdapter
    private lateinit var ordersRef: DatabaseReference

    // 🔥 Dynamic filter
    private var currentFilter = "pending"

    private val orderList = mutableListOf<OrderEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminOrdersScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ordersRef = FirebaseDatabase.getInstance().getReference("coffee_orders")

        adapter = OrderAdapter(
            orders = orderList,
            layoutRes = R.layout.activity_order_item_blueprint
        ) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }

        binding.recyclerOrders.layoutManager = LinearLayoutManager(this)
        binding.recyclerOrders.adapter = adapter

        // 🔥 Default load
        loadOrders(currentFilter)

        // 🔥 Buttons filter (ensure XML ma aa IDs che)
        binding.btnPending.setOnClickListener {
            currentFilter = "pending"
            loadOrders(currentFilter)
        }

        binding.btnDispatched.setOnClickListener {
            currentFilter = "dispatched"
            loadOrders(currentFilter)
        }

        binding.btnCompleted.setOnClickListener {
            currentFilter = "completed"
            loadOrders(currentFilter)
        }
    }

    // 🔥 Generic loader (Real time)
    private fun loadOrders(statusFilter: String) {

        ordersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                orderList.clear()

                for (userSnapshot in snapshot.children) {

                    val emailKey = userSnapshot.key ?: continue

                    for (orderSnapshot in userSnapshot.children) {

                        val order =
                            orderSnapshot.getValue(OrderEntity::class.java)

                        if (order != null) {

                            val status =
                                order.orderStatus?.trim()?.lowercase()

                            if (status == statusFilter) {

                                order.orderId =
                                    orderSnapshot.key ?: ""

                                order.email =
                                    emailKey.replace("_", ".")

                                orderList.add(order)
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged()

                // 🔥 Empty UI handling
                if (orderList.isEmpty()) {
                    binding.recyclerOrders.visibility = View.GONE
                    binding.tvEmptyOrders.visibility = View.VISIBLE
                } else {
                    binding.recyclerOrders.visibility = View.VISIBLE
                    binding.tvEmptyOrders.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@activity_admin_orders_screen,
                    "Failed to load orders",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun updateOrderStatus(order: OrderEntity, newStatus: String) {

        val emailKey = order.email.replace(".", "_")

        ordersRef.child(emailKey)
            .child(order.orderId)
            .child("orderStatus")
            .setValue(newStatus)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Order updated to $newStatus",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Failed to update order",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}