package com.example.caffeeborcelle.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*


class ProductViewModel : ViewModel() {

    private val coffeeRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("products")

    private val _allCoffee = MutableLiveData<List<ProductEntity>>()
    val allCoffee: LiveData<List<ProductEntity>> = _allCoffee

    private val coffeeListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {

            val list = mutableListOf<ProductEntity>()

            for (snap in snapshot.children) {

                val coffee = snap.getValue(ProductEntity::class.java)

                coffee?.let {
                    it.id = snap.key ?: ""   // 🔥 MOST IMPORTANT LINE
                    list.add(it)
                }
            }

            _allCoffee.postValue(list)
        }

        override fun onCancelled(error: DatabaseError) {
            _allCoffee.postValue(emptyList())
        }
    }

    init {
        coffeeRef.addValueEventListener(coffeeListener)
    }

    // 🔥 INSERT
    fun insert(coffee: ProductEntity) {
        val id = if (coffee.id.isNotBlank()) {
            coffee.id
        } else {
            coffeeRef.push().key ?: return
        }

        coffeeRef.child(id).setValue(coffee.copy(id = id))
    }

    // 🔥 UPDATE
    fun update(coffee: ProductEntity) {
        if (coffee.id.isBlank()) return
        coffeeRef.child(coffee.id).setValue(coffee)
    }

    // 🔥 DELETE
    fun delete(coffee: ProductEntity) {
        if (coffee.id.isBlank()) return
        coffeeRef.child(coffee.id).removeValue()
    }

    override fun onCleared() {
        super.onCleared()
        coffeeRef.removeEventListener(coffeeListener)
    }
}