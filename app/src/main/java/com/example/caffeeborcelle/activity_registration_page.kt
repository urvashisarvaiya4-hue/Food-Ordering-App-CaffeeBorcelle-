package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.databinding.ActivityRegistrationPageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class activity_registration_page : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // ✅ USER REGISTER
        binding.btnRegister.setOnClickListener {
            registerUser("user")
        }

        // ✅ ADMIN LOGIN PAGE
        binding.btnAdmin.setOnClickListener {
            startActivity(Intent(this, activity_management_login::class.java))
        }

        // ✅ LOGIN REDIRECT
        binding.txtLoginRedirect.setOnClickListener {
            startActivity(Intent(this, activity_login_page::class.java))
        }
    }

    private fun registerUser(role: String) {

        val name = binding.edtFullName.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirm = binding.edtConfirmPassword.text.toString().trim()

        // 🔥 Validation
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirm) {
            Toast.makeText(this, "Password not match", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔥 Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val uid = auth.currentUser?.uid ?: return@addOnSuccessListener

                // 🔥 User data
                val userMap = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "role" to role,
                    "createdAt" to System.currentTimeMillis()
                )

                // 🔥 Save in Realtime Database
                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .setValue(userMap)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Registered Successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Redirect to login
                        startActivity(Intent(this, activity_login_page::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            "Failed to save user",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }
}