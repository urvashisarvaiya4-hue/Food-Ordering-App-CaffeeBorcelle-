package com.example.caffeeborcelle

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caffeeborcelle.MyAdapter.OrderStatusAdapter
import com.example.caffeeborcelle.model.OrderEntity
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.*


class activity_order_product_status : AppCompatActivity() {


    private lateinit var adapter: OrderStatusAdapter
    private val orders = mutableListOf<OrderEntity>()

    private lateinit var rvOrders: RecyclerView
    private lateinit var tvEmptyOrders: TextView

    private lateinit var sessionManager: SessionManager
    private var currentUserEmail: String? = null

    private lateinit var ordersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_product_status)

        rvOrders = findViewById(R.id.rvOrderStatus)
        tvEmptyOrders = findViewById(R.id.EmptyOrders)

        rvOrders.layoutManager = LinearLayoutManager(this)
        adapter = OrderStatusAdapter(orders)
        rvOrders.adapter = adapter

        // ✅ SessionManager
        sessionManager = SessionManager(this)
        currentUserEmail = sessionManager.getUserEmail()

        if (currentUserEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val emailKey = currentUserEmail!!.replace(".", "_")

        // 🔥 Firebase node for CoffeeBorcelle
        ordersRef = FirebaseDatabase.getInstance()
            .getReference("coffee_orders")
            .child(emailKey)

        observeOrders()
    }

    // 🔥 Observe real-time orders
    private fun observeOrders() {
        ordersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                orders.clear()

                for (orderSnap in snapshot.children) {
                    val order = orderSnap.getValue(OrderEntity::class.java)
                    if (order != null) {
                        orders.add(order)
                    }
                }

                adapter.notifyDataSetChanged()

                if (orders.isEmpty()) {
                    rvOrders.visibility = View.GONE
                    tvEmptyOrders.visibility = View.VISIBLE
                } else {
                    rvOrders.visibility = View.VISIBLE
                    tvEmptyOrders.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@activity_order_product_status,
                    "Failed to load orders",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
