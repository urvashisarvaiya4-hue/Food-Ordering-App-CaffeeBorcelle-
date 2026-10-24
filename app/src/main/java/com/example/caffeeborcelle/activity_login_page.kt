package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.admin.activity_management_dashboard
import com.example.caffeeborcelle.databinding.ActivityLoginPageBinding
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class activity_login_page : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        binding.txtCreate.setOnClickListener {
            startActivity(Intent(this, activity_registration_page::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser?.uid

                if (uid == null) {
                    Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 🔥 Get user data from Realtime Database
                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .get()
                    .addOnSuccessListener { snapshot ->

                        if (snapshot.exists()) {

                            val role = snapshot.child("role").value.toString()
                            val name = snapshot.child("name").value.toString()

                            // ✅ Create Session
                            SessionManager(this).createLoginSession(email, name)

                            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                            if (role == "admin") {
                                startActivity(Intent(this, activity_management_dashboard::class.java))
                            } else {
                                startActivity(Intent(this, activity_home_page::class.java))
                            }

                            finish()

                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Database error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login Failed: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }
}