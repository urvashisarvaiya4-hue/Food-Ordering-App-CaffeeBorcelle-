package com.example.caffeeborcelle.model

data class AdminEntity (
    var id: Int = 0,        // Firebase key / admin id
    var username: String = "",
    var password: String = ""
)