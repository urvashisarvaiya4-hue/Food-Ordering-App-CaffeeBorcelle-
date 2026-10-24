package com.example.caffeeborcelle

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.FirebaseDatabase
import com.example.caffeeborcelle.R

class order_success : AppCompatActivity() {

    private lateinit var session: SessionManager
    private var loggedInEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        // ✅ Session
        session = SessionManager(this)
        loggedInEmail = session.getUserEmail()

        val paymentText: TextView = findViewById(R.id.paymentText)
        val totalText: TextView = findViewById(R.id.totalText)
        val okButton: Button = findViewById(R.id.okButton)

        // ✅ Get intent data
        val payment = intent.getStringExtra("PAYMENT") ?: "N/A"
        val total = intent.getDoubleExtra("TOTAL", 0.0)

        paymentText.text = "Payment: $payment"
        totalText.text = "Total: ₹$total"

        // ✅ Clear cart after order success
        okButton.setOnClickListener {

            loggedInEmail?.let { email  ->

                val emailKey = email.replace(".", "_")

                FirebaseDatabase.getInstance()
                    .getReference("coffee_carts")  // 🔥 coffee app node
                    .child(emailKey)
                    .removeValue()
            }

            finish()
        }
    }
}