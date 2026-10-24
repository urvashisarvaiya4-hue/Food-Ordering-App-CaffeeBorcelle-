package com.example.caffeeborcelle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caffeeborcelle.model.CartEntity
import com.google.firebase.database.*


class CartViewModel (
    private val userEmail: String
) : ViewModel() {

    // 🔥 Coffee app cart node
    private val cartRef =
        FirebaseDatabase.getInstance()
            .getReference("coffee_carts")
            .child(userEmail.replace(".", "_"))

    private val _cartItems = MutableLiveData<List<CartEntity>>()
    val cartItems: LiveData<List<CartEntity>> = _cartItems

    private val cartListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {

            val list = mutableListOf<CartEntity>()

            for (snap in snapshot.children) {
                val item = snap.getValue(CartEntity::class.java)
                if (item != null) {
                    list.add(item)
                }
            }

            _cartItems.postValue(list)
        }

        override fun onCancelled(error: DatabaseError) {
            _cartItems.postValue(emptyList())
        }
    }

    init {
        // 🔥 Real-time cart updates
        cartRef.addValueEventListener(cartListener)
    }

    override fun onCleared() {
        super.onCleared()
        cartRef.removeEventListener(cartListener)
    }
}