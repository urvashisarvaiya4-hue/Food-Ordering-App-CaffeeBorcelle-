package com.example.caffeeborcelle.ui

import com.example.caffeeborcelle.model.CartEntity
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*

object CartManager {

    private val cartRef =
        FirebaseDatabase.getInstance().getReference("coffee_carts")

    private fun userKey(email: String): String =
        email.replace(".", "_")

    // 🔥 Get cart items
    fun getCartItems(
        email: String,
        onResult: (List<CartEntity>) -> Unit
    ) {
        cartRef.child(userKey(email))
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = mutableListOf<CartEntity>()

                    for (snap in snapshot.children) {
                        val item =
                            snap.getValue(CartEntity::class.java)
                        if (item != null) list.add(item)
                    }

                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    // 🔥 Add to cart (Increase quantity if exists)
    fun addToCart(
        email: String,
        product: ProductEntity,
        onResult: (Boolean) -> Unit
    ) {

        val ref =
            cartRef.child(userKey(email)).child(product.id)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val currentQty =
                    snapshot.child("quantity")
                        .getValue(Long::class.java) ?: 0L

                val cartItem = CartEntity(
                    productId = product.id,
                    productName = product.name,
                    productImage = product.imageUrl,
                    price = product.price.toLong(),
                    quantity = currentQty + 1L
                )

                ref.setValue(cartItem)
                    .addOnSuccessListener { onResult(true) }
                    .addOnFailureListener { onResult(false) }
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(false)
            }
        })
    }

    // 🔥 Remove single product
    fun removeFromCart(email: String, productId: String) {
        cartRef.child(userKey(email))
            .child(productId)
            .removeValue()
    }

    // 🔥 Clear entire cart
    fun clearCart(email: String) {
        cartRef.child(userKey(email)).removeValue()
    }

}