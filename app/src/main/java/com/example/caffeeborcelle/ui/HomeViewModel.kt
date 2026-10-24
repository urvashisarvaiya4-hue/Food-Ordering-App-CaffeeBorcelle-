package com.example.caffeeborcelle.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*

class HomeViewModel : ViewModel() {

    // ☕ Firebase coffee node
    private val coffeeRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("coffee_products")

    private val _coffeeProducts = MutableLiveData<List<ProductEntity>>()
    val coffeeProducts: LiveData<List<ProductEntity>> = _coffeeProducts

    private val coffeeListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {

            val list = mutableListOf<ProductEntity>()

            for (snap in snapshot.children) {
                val coffee = snap.getValue(ProductEntity::class.java)

                if (coffee != null) {
                    list.add(coffee)
                }
            }

            _coffeeProducts.postValue(list)
        }

        override fun onCancelled(error: DatabaseError) {
            _coffeeProducts.postValue(emptyList())
        }
    }

    init {
        coffeeRef.addValueEventListener(coffeeListener)
    }

    override fun onCleared() {
        super.onCleared()
        coffeeRef.removeEventListener(coffeeListener)
    }
}