package com.example.caffeeborcelle.model

class CartEntity (
    var productId: String? = "",
    var productName: String? = "",
    //var productPrice: String? = "",
    var productImage: String? = "",
    var price: Long? = 0L,
    var quantity: Long? = 1L
)