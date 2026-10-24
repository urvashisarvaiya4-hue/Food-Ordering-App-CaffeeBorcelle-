package com.example.caffeeborcelle.model

data class UserEntity (

    // ------------------- User Email (Primary Key) -------------------
    var uid: String? = null,
    val email: String = "",

    // ------------------- User Details -------------------
    val name: String = "",
    var password: String = "",
    var profileImage: String? = null   // 🔥 MUST
)
