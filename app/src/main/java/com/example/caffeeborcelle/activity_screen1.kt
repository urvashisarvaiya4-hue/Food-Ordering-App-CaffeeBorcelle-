package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class activity_screen1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_screen1)

        val next = findViewById<ImageView>(R.id.btnNext)

        next.setOnClickListener {
            val intent = Intent(this, activity_screen2::class.java)
            startActivity(intent)
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        }
        fun loadFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainContainer, fragment)
                .addToBackStack(null)
                .commit()
        }}
}