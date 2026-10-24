package com.example.caffeeborcelle

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.databinding.ActivityDetailItemBlueprintBinding
import com.example.caffeeborcelle.model.CartEntity
import com.example.caffeeborcelle.model.ProductEntity
import com.example.caffeeborcelle.model.WishlistEntity
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.*

class ItemDetails : AppCompatActivity() {

    private lateinit var binding: ActivityDetailItemBlueprintBinding
private var currentProduct: ProductEntity? = null

private lateinit var productRef: DatabaseReference
private lateinit var cartRef: DatabaseReference
private lateinit var wishlistRef: DatabaseReference

private val userEmail: String by lazy { SessionManager(this).getUserEmail() ?: "" }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityDetailItemBlueprintBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.imageButton.setOnClickListener { finish() }

    val productId = intent.getStringExtra("productId")
    if (productId.isNullOrEmpty()) {
        Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show()
        finish()
        return
    }

    productRef = FirebaseDatabase.getInstance()
        .getReference("products")
        .child(productId)

    loadProduct()

    binding.addToCartButton.setOnClickListener { addToCart() }
    binding.wishlistButton.setOnClickListener { addToWishlist() }
}

private fun loadProduct() {
    productRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val product = snapshot.getValue(ProductEntity::class.java)
            if (product == null) {
                Toast.makeText(this@ItemDetails, "Product not found", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            currentProduct = product
            bindProduct(product)
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(this@ItemDetails, "Failed to load product", Toast.LENGTH_SHORT).show()
            finish()
        }
    })
}

private fun addToCart() {
    val product = currentProduct ?: return
    if (userEmail.isEmpty()) { Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show(); return }

    val emailKey = userEmail.replace(".", "_")
    cartRef = FirebaseDatabase.getInstance()
        .getReference("coffee_carts")
        .child(emailKey)
        .child(product.id)

    cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val qty = snapshot.child("quantity").getValue(Long::class.java) ?: 0L
            val cartItem = CartEntity(
                productId = product.id,
                productName = product.name,
                productImage = product.imageUrl,
                price = product.price.toLong(),
                quantity = qty + 1
            )
            cartRef.setValue(cartItem).addOnSuccessListener {
                Toast.makeText(this@ItemDetails,
                    if (qty == 0L) "Added to cart" else "Cart updated",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}

private fun addToWishlist() {
    val product = currentProduct ?: return
    val anim = AnimationUtils.loadAnimation(this, R.anim.animation)
    binding.wishlistButton.startAnimation(anim)

    if (userEmail.isEmpty()) { Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show(); return }

    val emailKey = userEmail.replace(".", "_")
    wishlistRef = FirebaseDatabase.getInstance()
        .getReference("coffee_wishlists")
        .child(emailKey)
        .child(product.id)

    wishlistRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                Toast.makeText(this@ItemDetails, "Already in wishlist", Toast.LENGTH_SHORT).show()
                return
            }
            val wishlistItem = WishlistEntity(
                id = product.id,
                name = product.name,
                imageUrl = product.imageUrl,
                price = product.price
            )
            wishlistRef.setValue(wishlistItem).addOnSuccessListener {
                Toast.makeText(this@ItemDetails, "Added to wishlist", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}

private fun bindProduct(product: ProductEntity) {
    binding.detailProductNameTextView.text = product.name
    binding.detailProductPriceTextView.text = "₹${product.price}"
    binding.shortDescriptionTextView.text = product.shortDesc
    //binding.fullDescriptionTextView.text = product.fullDesc

    Glide.with(this)
        .load(product.imageUrl)
        .placeholder(android.R.drawable.ic_menu_gallery)  // ✅ Android built-in
        .error(android.R.drawable.ic_menu_gallery)
        .into(binding.detailProductImageView)
}}