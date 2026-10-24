package com.example.caffeeborcelle

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.databinding.ActivityHelpCenterBinding

class HelpCenterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpCenterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpCenterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContactSupport.setOnClickListener {

            // Email intent
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@coffeeborcelle.com")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }

            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}