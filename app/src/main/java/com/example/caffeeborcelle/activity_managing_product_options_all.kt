package com.example.caffeeborcelle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caffeeborcelle.AdminAdapter.AdapterManagementALL
import com.example.caffeeborcelle.databinding.ActivityManagingProductOptionsAllBinding
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*


class activity_managing_product_options_all : AppCompatActivity() {

    private lateinit var binding: ActivityManagingProductOptionsAllBinding
    private lateinit var adapter: AdapterManagementALL
    private val productList = mutableListOf<ProductEntity>()

    private val productRef: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("products")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityManagingProductOptionsAllBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔙 Back button
        binding.backButton.setOnClickListener { finish() }

        // 🔥 Adapter setup
        adapter = AdapterManagementALL(productList, this)
        binding.menuRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.menuRecyclerView.adapter = adapter

        loadAllProducts()
    }

    // 🔥 Load all coffee / food items from Firebase
    private fun loadAllProducts() {

        productRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                productList.clear()

                for (productSnap in snapshot.children) {

                    val product = productSnap.getValue(ProductEntity::class.java)

                    if (product != null) {
                        product.id = productSnap.key!!   // 🔥 MOST IMPORTANT LINE
                        productList.add(product)
                    }
                }

                adapter.updateProductList(productList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Optional error handling
            }
        })

}
    }
