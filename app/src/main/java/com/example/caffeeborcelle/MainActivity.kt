package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val logo = findViewById<ImageView>(R.id.logoimage)
        val steam = findViewById<ImageView>(R.id.steamimage)



        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
        val steamAnim = AnimationUtils.loadAnimation(this, R.anim.steam_anim)

        logo.startAnimation(logoAnim)
        steam.startAnimation(steamAnim)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, activity_screen1::class.java))
            finish()
        }, 2500)
    }
}