package com.example.caffeeborcelle.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.caffeeborcelle.model.CartEntity
import com.google.firebase.database.*

class CartRepository {

    private val cartsRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("coffee_carts")

    // 🔥 Get all cart items
    fun getAllCartItems(userEmail: String): LiveData<List<CartEntity>> {

        val result = MutableLiveData<List<CartEntity>>()
        val emailKey = userEmail.replace(".", "_")

        cartsRef.child(emailKey)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val list = mutableListOf<CartEntity>()

                    for (snap in snapshot.children) {
                        val item = snap.getValue(CartEntity::class.java)
                        if (item != null) list.add(item)
                    }

                    result.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    result.postValue(emptyList())
                }
            })

        return result
    }

    // 🔥 Insert item
    fun insert(userEmail: String, cartItem: CartEntity) {
        val emailKey = userEmail.replace(".", "_")

        cartsRef.child(emailKey)
            .child(cartItem.productId.toString())
            .setValue(cartItem)
    }

    // 🔥 Delete item
    fun delete(userEmail: String, cartItem: CartEntity) {
        val emailKey = userEmail.replace(".", "_")

        cartsRef.child(emailKey)
            .child(cartItem.productId.toString())
            .removeValue()
    }

    // 🔥 Update item
    fun update(userEmail: String, cartItem: CartEntity) {
        val emailKey = userEmail.replace(".", "_")

        cartsRef.child(emailKey)
            .child(cartItem.productId.toString())
            .setValue(cartItem)
    }

    // 🔥 Update quantity
    fun updateQuantity(
        cartId: Int,
        quantity: Int,
        userEmail: String
    ) {
        val emailKey = userEmail.replace(".", "_")

        cartsRef.child(emailKey)
            .child(cartId.toString())
            .child("quantity")
            .setValue(quantity)
    }

    // 🔥 Clear cart after order
    fun clearCart(userEmail: String) {
        val emailKey = userEmail.replace(".", "_")
        cartsRef.child(emailKey).removeValue()
    }
}
