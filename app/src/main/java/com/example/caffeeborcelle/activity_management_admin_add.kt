package com.example.caffeeborcelle

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.databinding.ActivityManagementAdminAddBinding
import com.example.caffeeborcelle.model.AdminEntity
import com.google.firebase.database.FirebaseDatabase

class activity_management_admin_add : AppCompatActivity() {

    private lateinit var binding: ActivityManagementAdminAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagementAdminAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adminRef = FirebaseDatabase.getInstance().getReference("admins")

        binding.btnAddAdmin.setOnClickListener {

            val username = binding.etAdminUsername.text.toString().trim()
            val password = binding.etAdminPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 CoffeeBorcelle admin model
            val admin = AdminEntity(username = username, password = password)

            adminRef.child(username)
                .setValue(admin)
                .addOnSuccessListener {
                    Toast.makeText(this, "Admin added successfully", Toast.LENGTH_SHORT).show()
                    //binding.etAdminUsername.text.clear()
                    //binding.etAdminPassword.text.clear()
                    binding.etAdminUsername.text?.clear()
                    binding.etAdminPassword.text?.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add admin", Toast.LENGTH_SHORT).show()
                }
        }
    }
}