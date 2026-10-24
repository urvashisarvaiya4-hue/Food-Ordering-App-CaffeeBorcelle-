package com.example.caffeeborcelle.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*

class ProductRepository {

    // 🔥 IMPORTANT → SAME node admin & user
    private val productsRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("products")

    private val _allProducts = MutableLiveData<List<ProductEntity>>()
    val allProducts: LiveData<List<ProductEntity>> = _allProducts

    private var listener: ValueEventListener? = null

    init {
        observeProducts()
    }

    // 🔥 REALTIME OBSERVER
    private fun observeProducts() {

        listener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<ProductEntity>()

                for (snap in snapshot.children) {
                    val product = snap.getValue(ProductEntity::class.java)
                    product?.let { list.add(it) }
                }

                _allProducts.postValue(list)
                println("Snapshot size: ${snapshot.childrenCount}")
            }

            override fun onCancelled(error: DatabaseError) {
                _allProducts.postValue(emptyList())
            }
        }

        productsRef.addValueEventListener(listener!!)
    }

    // 🔥 INSERT product
    fun insert(product: ProductEntity) {

        val id = if (product.id.isNotEmpty()) {
            product.id
        } else {
            productsRef.push().key ?: return
        }

        val updated = product.copy(id = id)
        productsRef.child(id).setValue(updated)
    }

    // 🔥 UPDATE
    fun update(product: ProductEntity) {
        if (product.id.isEmpty()) return
        productsRef.child(product.id).setValue(product)
    }

    // 🔥 DELETE
    fun delete(product: ProductEntity) {
        if (product.id.isEmpty()) return
        productsRef.child(product.id).removeValue()
    }

    // 🔥 SEARCH (safe)
    fun searchProducts(query: String): LiveData<List<ProductEntity>> {

        val result = MutableLiveData<List<ProductEntity>>()

        productsRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = mutableListOf<ProductEntity>()

                for (snap in snapshot.children) {
                    val product = snap.getValue(ProductEntity::class.java)

                    val name = product?.name ?: ""

                    if (name.contains(query, ignoreCase = true)) {
                        product?.let { list.add(it) }
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

    // 🔥 CLEANUP (important)
    fun removeListener() {
        listener?.let { productsRef.removeEventListener(it) }
    }
}