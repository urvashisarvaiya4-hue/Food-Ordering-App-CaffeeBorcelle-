package com.example.caffeeborcelle.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.caffeeborcelle.model.UserEntity
import com.google.firebase.database.*

class UserViewModel (application: Application) : AndroidViewModel(application) {

    private val usersRef =
        FirebaseDatabase.getInstance().getReference("users")

    private val ordersRef =
        FirebaseDatabase.getInstance().getReference("coffee_orders")

    private val wishlistRef =
        FirebaseDatabase.getInstance().getReference("coffee_wishlists")

    private val _allUsers = MutableLiveData<List<UserEntity>>()
    val allUsers: LiveData<List<UserEntity>> = _allUsers

    init {
        // 🔥 Real-time users list
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<UserEntity>()

                for (snap in snapshot.children) {
                    val user =
                        snap.getValue(UserEntity::class.java)
                    if (user != null) list.add(user)
                }

                _allUsers.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔹 Get user by email
    fun getUserByEmail(email: String): LiveData<UserEntity> {
        val result = MutableLiveData<UserEntity>()
        val emailKey = email.replace(".", "_")

        usersRef.child(emailKey)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val user =
                        snapshot.getValue(UserEntity::class.java)

                    if (user != null) {
                        result.postValue(user)
                    }}

                override fun onCancelled(error: DatabaseError) {}
            })

        return result
    }

    // 🔹 Coffee orders count
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

    // 🔹 Wishlist count
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

    // 🔹 Update user name
    fun updateUserNameByEmail(email: String, newName: String) {
        val emailKey = email.replace(".", "_")
        usersRef.child(emailKey).child("name").setValue(newName)
    }
}