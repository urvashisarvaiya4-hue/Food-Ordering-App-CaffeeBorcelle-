package com.example.caffeeborcelle.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*

class SearchViewModel : ViewModel() {

    // ☕ Firebase coffee node
    private val coffeeRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("coffee_products")

    private val _allCoffee = MutableLiveData<List<ProductEntity>>()
    private val _searchResults = MutableLiveData<List<ProductEntity>>()

    // ☕ Load all coffee products
    fun getAllCoffee(): LiveData<List<ProductEntity>> {

        coffeeRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<ProductEntity>()

                for (snap in snapshot.children) {
                    val coffee =
                        snap.getValue(ProductEntity::class.java)

                    if (coffee != null) list.add(coffee)
                }

                _allCoffee.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                _allCoffee.postValue(emptyList())
            }
        })

        return _allCoffee
    }

    // ☕ Search coffee by name
    fun searchCoffee(query: String): LiveData<List<ProductEntity>> {

        coffeeRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<ProductEntity>()

                for (snap in snapshot.children) {

                    val coffee =
                        snap.getValue(ProductEntity::class.java)

                    if (coffee != null &&
                        coffee.name.contains(query, ignoreCase = true)
                    ) {
                        list.add(coffee)
                    }
                }

                _searchResults.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                _searchResults.postValue(emptyList())
            }
        })

        return _searchResults
    }
}