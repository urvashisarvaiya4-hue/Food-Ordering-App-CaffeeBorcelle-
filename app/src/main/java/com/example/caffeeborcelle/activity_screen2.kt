package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_screen2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen2)

        val next = findViewById<ImageView>(R.id.btnNext1)

        next.setOnClickListener {
            val intent = Intent(this, activity_registration_page::class.java)
            startActivity(intent)
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        }
    }
}