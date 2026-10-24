package com.example.caffeeborcelle

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.databinding.ActivityRewardsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RewardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRewardsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser.uid
        database = FirebaseDatabase.getInstance().getReference("Users")

        // 🔥 Fetch Points from Firebase
        database.child(uid).child("points")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()) {
                        val points = snapshot.getValue(Int::class.java) ?: 0
                        binding.txtPoints.text = points.toString()
                    } else {
                        binding.txtPoints.text = "0"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RewardsActivity,
                        "Failed to load points",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }
}