package com.example.caffeeborcelle

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caffeeborcelle.databinding.ActivityAdminUserDetailBinding
import com.example.caffeeborcelle.model.CartEntity
import com.example.caffeeborcelle.model.WishlistEntity
import com.example.caffeeborcelle.model.OrderEntity
import com.google.firebase.database.*

class activity_admin_user_detail : AppCompatActivity() {

    private lateinit var binding: ActivityAdminUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "No user email provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val emailKey = email.replace(".", "_")

        binding.userEmail.text = email
        binding.topAppBar.setNavigationOnClickListener { finish() }

        binding.cartRecycler.layoutManager = GridLayoutManager(this, 2)
        binding.wishlistRecycler.layoutManager = GridLayoutManager(this, 2)
        binding.ordersRecycler.layoutManager = GridLayoutManager(this, 1)

        loadCart(emailKey)
        loadWishlist(emailKey)
        loadOrders(emailKey)
    }

    private fun loadCart(emailKey: String) {
        FirebaseDatabase.getInstance()
            .getReference("carts")
            .child(emailKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<CartEntity>()
                    for (snap in snapshot.children) {
                        snap.getValue(CartEntity::class.java)?.let { list.add(it) }
                    }

                    binding.cartRecycler.adapter =
                        AdminUserDetailCartAdapter(list)

                    binding.cartTitle.text = "Cart (${list.size})"
                    binding.emptyCart.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadWishlist(emailKey: String) {
        FirebaseDatabase.getInstance()
            .getReference("wishlists")
            .child(emailKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<WishlistEntity>()
                    for (snap in snapshot.children) {
                        snap.getValue(WishlistEntity::class.java)?.let { list.add(it) }
                    }

                    binding.wishlistRecycler.adapter =
                        AdminUserDetailWishlistAdapter(list)

                    binding.wishlistTitle.text = "Wishlist (${list.size})"
                    binding.emptyWishlist.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Show ALL orders (Pending / Delivered / Cancelled)
    private fun loadOrders(emailKey: String) {
        FirebaseDatabase.getInstance()
            .getReference("coffee_orders")
            .child(emailKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<OrderEntity>()

                    for (snap in snapshot.children) {
                        val order = snap.getValue(OrderEntity::class.java)
                        if (order != null) {
                            list.add(order)
                        }
                    }

                    list.reverse()

                    binding.ordersRecycler.adapter =
                        AdminUserDetailOrdersAdapter(list)

                    binding.ordersTitle.text = "Orders (${list.size})"
                    binding.emptyOrders.visibility =
                        if (list.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
