package com.example.caffeeborcelle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.caffeeborcelle.AdminAdapter.OrderAdapter
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.model.OrderEntity
import com.google.firebase.database.*

class activity_delivered : AppCompatActivity() {

    private lateinit var adapter: OrderAdapter
    private lateinit var ordersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivered)

        val recycler = findViewById<RecyclerView>(R.id.recyclerDeliveredOrders)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = OrderAdapter(
            orders = mutableListOf(),
            layoutRes = R.layout.activity_delivered_blueprint,
            onStatusChange = { _, _ -> } // ☕ Delivered orders → update nahi
        )

        recycler.adapter = adapter

        loadDeliveredOrders()
    }

    private fun loadDeliveredOrders() {

        ordersRef = FirebaseDatabase.getInstance()
            .getReference("coffee_orders") // ⚠️ OPTIONAL → jo tame node change kari hoy

        ordersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val deliveredOrders = mutableListOf<OrderEntity>()

                for (userSnap in snapshot.children) {
                    for (orderSnap in userSnap.children) {

                        val order = orderSnap.getValue(OrderEntity::class.java)

                        if (order?.orderStatus == "Delivered") {
                            deliveredOrders.add(order)
                        }
                    }
                }

                adapter.updateList(deliveredOrders)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}