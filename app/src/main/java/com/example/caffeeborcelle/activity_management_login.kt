package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caffeeborcelle.admin.activity_management_dashboard
import com.example.caffeeborcelle.databinding.ActivityManagementLoginBinding
import com.example.caffeeborcelle.model.AdminEntity
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.*

class activity_management_login : AppCompatActivity() {

    private val binding: ActivityManagementLoginBinding by lazy {
        ActivityManagementLoginBinding.inflate(layoutInflater)
    }

    private lateinit var adminRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 🔥 Firebase reference
        adminRef = FirebaseDatabase.getInstance().getReference("admins")

        // Default admin create
        ensureDefaultAdmin()

        binding.ManagementLoginButton.setOnClickListener {

            val username = binding.ManagementUsername.text?.toString()?.trim() ?: ""
            val password = binding.ManagementPassword.text?.toString()?.trim() ?: ""

            if (username.isEmpty() || password.isEmpty()) {
                showToast("Please enter username and password")
                return@setOnClickListener
            }

            loginAdmin(username, password)
        }
    }

    // 🔥 Default admin
    private fun ensureDefaultAdmin() {
        adminRef.child("admin").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val admin = AdminEntity(username = "admin", password = "admin123")
                    adminRef.child("admin").setValue(admin)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 🔥 Login logic
    private fun loginAdmin(username: String, password: String) {

        adminRef.child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val admin = snapshot.getValue(AdminEntity::class.java)

                    if (admin != null && admin.password == password) {

                        showToast("Login Successful")

                        startActivity(
                            Intent(
                                this@activity_management_login,
                                activity_management_dashboard::class.java
                            )
                        )
                        finish()

                    } else {
                        showToast("Invalid username or password")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showToast("Login failed")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}


