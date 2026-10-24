package com.example.caffeeborcelle.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderEntity (

    // ------------------- Order ID -------------------
    var orderId: String = "",

    // ------------------- Customer Details -------------------
    val fullName: String = "",
    var email: String = "",
    val contact: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",

    // ------------------- Payment & Status -------------------
    val paymentMethod: String = "",
    val orderStatus: String = "Pending",
    val orderDate: String = "",

    // ------------------- Product Details -------------------
    val productNames: String = "",
    val productImageUrl: String = "",

    // ------------------- Pricing -------------------
    val totalPrice: Double = 0.0,

    // ------------------- Timestamp -------------------
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun getCurrentDate(): String {
            val sdf =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}
