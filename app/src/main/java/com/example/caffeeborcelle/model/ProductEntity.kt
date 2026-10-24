package com.example.caffeeborcelle.model

 data class ProductEntity (
    var id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val discount: Double = 0.0,
    val rating: Double = 0.0,

    val reviews: Int = 0,
    val category: String = "",   // 🔥 THIS IS REQUIRED
    val shortDesc: String = "",
    val fullDesc: String = "",
    //val keyFeatures: String = ""
)