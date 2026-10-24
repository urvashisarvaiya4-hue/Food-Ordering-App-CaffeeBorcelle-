package com.example.caffeeborcelle.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.caffeeborcelle.model.UserEntity
import com.google.firebase.database.*

class UserRepository {

    private val usersRef =
        FirebaseDatabase.getInstance().getReference("coffee_users")

    private val ordersRef =
        FirebaseDatabase.getInstance().getReference("coffee_orders")

    private val wishlistRef =
        FirebaseDatabase.getInstance().getReference("coffee_wishlists")

    // 🔥 Get all users (Admin panel)
    fun getAllUsers(): LiveData<List<UserEntity>> {
        val result = MutableLiveData<List<UserEntity>>()

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<UserEntity>()

                for (snap in snapshot.children) {
                    val user = snap.getValue(UserEntity::class.java)

                    if (user != null) {
                        user.uid = snap.key   // 🔥 IMPORTANT LINE
                        list.add(user)
                    }
                }

                result.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                result.postValue(emptyList())
            }
        })

        return result
    }

    // 🔥 Insert
    fun insert(user: UserEntity) {
        val emailKey = user.email.replace(".", "_")
        usersRef.child(emailKey).setValue(user)
    }

    // 🔥 Update
    fun update(user: UserEntity) {
        val emailKey = user.email.replace(".", "_")
        usersRef.child(emailKey).setValue(user)
    }

    // 🔥 Delete
    fun delete(user: UserEntity) {
        val emailKey = user.email.replace(".", "_")
        usersRef.child(emailKey).removeValue()
    }

    // 🔥 Get user by email
    fun getUserByEmail(email: String): LiveData<UserEntity> {
        val result = MutableLiveData<UserEntity>()
        val emailKey = email.replace(".", "_")

        usersRef.child(emailKey)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    result.postValue(
                        snapshot.getValue(UserEntity::class.java)
                    )
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        return result
    }

    // 🔥 Order count
    fun getOrderCountByEmail(email: String): LiveData<Int> {
        val result = MutableLiveData<Int>()
        val emailKey = email.replace(".", "_")

        ordersRef.child(emailKey)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    result.postValue(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                    result.postValue(0)
                }
            })

        return result
    }

    // 🔥 Wishlist count
    fun getWishlistCount(email: String): LiveData<Int> {
        val result = MutableLiveData<Int>()
        val emailKey = email.replace(".", "_")

        wishlistRef.child(emailKey)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    result.postValue(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {
                    result.postValue(0)
                }
            })

        return result
    }

    // 🔥 Update name
    fun updateUserNameByEmail(email: String, newName: String) {
        val emailKey = email.replace(".", "_")
        usersRef.child(emailKey).child("name").setValue(newName)
    }

}