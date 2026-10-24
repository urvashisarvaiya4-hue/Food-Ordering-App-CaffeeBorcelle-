package com.example.caffeeborcelle.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.AdminFragment.LogoutDialogFragment
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.activity_admin_orders_screen
import com.example.caffeeborcelle.activity_delivered
import com.example.caffeeborcelle.activity_management_admin_add
import com.example.caffeeborcelle.activity_management_login
import com.example.caffeeborcelle.activity_managing_product_options_add
import com.example.caffeeborcelle.activity_managing_product_options_all
import com.example.caffeeborcelle.activity_user_management
import com.example.caffeeborcelle.databinding.ActivityManagementDashboardBinding
import com.example.caffeeborcelle.databinding.ActivityManagementDashboardBlueprintBinding
import com.example.caffeeborcelle.management_dashboard_blueprint
import com.google.firebase.database.*

class activity_management_dashboard : AppCompatActivity() {

    private val binding: ActivityManagementDashboardBinding by lazy {
        ActivityManagementDashboardBinding.inflate(layoutInflater)
    }

    private lateinit var ordersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 🔥 Firebase orders reference (Coffee Orders)
        ordersRef = FirebaseDatabase.getInstance().getReference("coffee_orders")

        // 🔥 Dashboard Cards
        setCard(binding.ProductManagementADD, R.drawable.inventory, "Add Coffee / Food")
        setCard(binding.ProductManagementALL, R.drawable.all_item, "All Menu")
        setCard(binding.ProductManagementDelivered, R.drawable.delivery_truck, "Delivered Orders")
        setCard(binding.UserInfo, R.drawable.accountcircle, "Customers")
        setCard(binding.AdminManagementADD, R.drawable.manage_account, "Add Admin")
        setCard(binding.AdminManagementSignOut, R.drawable.logout, "Log Out")

        // 🔥 Navigation
        binding.ProductManagementADD.root.setOnClickListener {
            startActivity(Intent(this, activity_managing_product_options_add::class.java))
        }

        binding.ProductManagementALL.root.setOnClickListener {
            startActivity(Intent(this, activity_managing_product_options_all::class.java))
        }

        binding.UserInfo.root.setOnClickListener {
            startActivity(Intent(this, activity_user_management::class.java))
        }

        binding.AdminManagementADD.root.setOnClickListener {
            startActivity(Intent(this, activity_management_admin_add::class.java))
        }

        binding.AdminManagementSignOut.root.setOnClickListener {
            LogoutDialogFragment().show(supportFragmentManager, "LogoutDialog")
        }

        binding.ProductManagementDelivered.root.setOnClickListener {
            startActivity(Intent(this, activity_admin_orders_screen::class.java))
        }

        binding.PendingStatus.setOnClickListener {
            startActivity(Intent(this, activity_admin_orders_screen::class.java))
        }

        binding.CompletedStatus.setOnClickListener {
            startActivity(Intent(this, activity_delivered::class.java))
        }

        // 🔥 Dashboard Data
        fetchPendingOrders()
        fetchDeliveredOrders()
        fetchTotalRevenue()
    }

    // 🔥 Pending + Preparing + Out for delivery
    private fun fetchPendingOrders() {
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var pending = 0

                for (user in snapshot.children) {
                    for (order in user.children) {

                        val status = order.child("orderStatus")
                            .getValue(String::class.java)

                        if (status != "Delivered") {
                            pending++
                        }
                    }
                }

                binding.tvPendingCount.text = pending.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔥 Delivered Count
    private fun fetchDeliveredOrders() {
        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var delivered = 0

                for (user in snapshot.children) {
                    for (order in user.children) {
                        if (order.child("orderStatus")
                                .getValue(String::class.java) == "Delivered"
                        ) {
                            delivered++
                        }
                    }
                }

                binding.Delivered.text = delivered.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔥 Total Earnings (Coffee Shop Revenue)
    private fun fetchTotalRevenue() {

        ordersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                var total = 0L

                for (user in snapshot.children) {
                    for (order in user.children) {

                        val status = order.child("orderStatus")
                            .getValue(String::class.java)

                        if (status == "Delivered") {

                            val amountAny =
                                order.child("totalAmount").value
                                    ?: order.child("totalPrice").value

                            val amount = when (amountAny) {
                                is Long -> amountAny
                                is String -> amountAny.toLongOrNull() ?: 0L
                                is Double -> amountAny.toLong()
                                else -> 0L
                            }

                            total += amount
                        }
                    }
                }

                binding.TotalEarningsAmount.text = "₹$total"
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔥 Card UI Setup
    private fun setCard(
        card: ActivityManagementDashboardBlueprintBinding,
        icon: Int,
        text: String
    ) {
        card.cardIcon.setImageResource(icon)
        card.cardText.text = text
    }
}