package com.example.caffeeborcelle

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.ActivityManageProductDetailsBinding
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*

class activity_manage_product_details : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        val foodId = intent.getStringExtra("productId")
        if (foodId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid item", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadFood(foodId)

        binding.imageButton.setOnClickListener { finish() }
    }

    // 🔥 Load coffee / food from Firebase
    private fun loadFood(foodId: String) {
        FirebaseDatabase.getInstance()
            .getReference("menu") // ✅ CoffeeBorcelle Firebase node
            .child(foodId)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val food = snapshot.getValue(ProductEntity::class.java)
                    if (food != null) {
                        showFoodDetails(food)
                    } else {
                        Toast.makeText(
                            this@activity_manage_product_details,
                            "Item not found",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@activity_manage_product_details,
                        "Failed to load",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // 🔥 Show details
    private fun showFoodDetails(food: ProductEntity) {

        binding.detailProductNameTextView.text = food.name
        binding.detailProductPriceTextView.text = "₹${food.price}"

        binding.shortDescriptionTextView.text =
            food.shortDesc.ifEmpty { "No description" }

        binding.fullDescriptionTextView.text =
            food.fullDesc.ifEmpty { "No full details" }


        Glide.with(this)
            .load(food.imageUrl)
            .placeholder(R.drawable.broken_image)
            .error(R.drawable.broken_image)
            .into(binding.detailProductImageView)
    }
}

