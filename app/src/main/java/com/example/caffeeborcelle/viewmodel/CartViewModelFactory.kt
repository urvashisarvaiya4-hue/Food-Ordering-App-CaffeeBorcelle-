package com.example.caffeeborcelle.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CartViewModelFactory (
    private val userEmail: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return CartViewModel(userEmail) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}