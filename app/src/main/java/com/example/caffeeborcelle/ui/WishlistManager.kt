package com.example.caffeeborcelle.ui

import com.example.caffeeborcelle.model.WishlistEntity
import com.google.firebase.database.*

object WishlistManager {
    private val database =
        FirebaseDatabase.getInstance().getReference("coffee_wishlist")

    // 🔑 Email → Firebase-safe key
    private fun getUserKey(email: String): String =
        email.replace(".", "_")

    // 🔥 GET WISHLIST
    fun getWishlist(
        email: String,
        onResult: (List<WishlistEntity>) -> Unit
    ) {
        val userKey = getUserKey(email)

        database.child(userKey)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<WishlistEntity>()
                    for (snap in snapshot.children) {
                        val item =
                            snap.getValue(WishlistEntity::class.java)
                        if (item != null) list.add(item)
                    }
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    // 🔥 ADD TO WISHLIST
    fun addToWishlist(email: String, item: WishlistEntity) {
        val userKey = getUserKey(email)

        database.child(userKey)
            .child(item.id)
            .setValue(item)
    }

    // 🔥 REMOVE FROM WISHLIST
    fun removeFromWishlist(email: String, itemId: String) {
        val userKey = getUserKey(email)

        database.child(userKey)
            .child(itemId)
            .removeValue()
    }

}