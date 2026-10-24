package com.example.caffeeborcelle.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class CartViewModel : ViewModel() {

    private val _cartText =
        MutableLiveData("Your coffee cart is empty ☕")
    val cartText: LiveData<String> get() = _cartText

    private var cartListener: ValueEventListener? = null
    private var cartRef: DatabaseReference? = null

    // 🔥 Call once from CartFragment
    fun observeCart(email: String) {

        val emailKey = email.replace(".", "_")

        cartRef = FirebaseDatabase.getInstance()
            .getReference("coffee_carts")
            .child(emailKey)

        cartListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists() ||
                    snapshot.childrenCount == 0L
                ) {
                    _cartText.postValue(
                        "Your coffee cart is empty ☕"
                    )
                } else {
                    _cartText.postValue("")
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        cartRef?.addValueEventListener(cartListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        cartListener?.let {
            cartRef?.removeEventListener(it)
        }
    }
}