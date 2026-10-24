package com.example.caffeeborcelle

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.caffeeborcelle.util.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class activity_edit_profile : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvChangePhoto: TextView
    private lateinit var tilName: TextInputLayout
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSave: MaterialButton

    private var selectedImageUri: Uri? = null
    private lateinit var userRef: DatabaseReference
    private lateinit var emailKey: String

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                imgProfile.setImageURI(it)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        imgProfile = findViewById(R.id.imgProfile)
        tvChangePhoto = findViewById(R.id.tvChangePhoto)
        tilName = findViewById(R.id.tilName)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        btnSave = findViewById(R.id.btnSave)



        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val email = currentUser.email!!

        emailKey = email.replace(".", "_")
        etEmail.setText(email)
        etEmail.isEnabled = false

        userRef = FirebaseDatabase.getInstance()
            .getReference("coffee_users")
            .child(emailKey)

        loadUserData()

        imgProfile.setOnClickListener { imagePicker.launch("image/*") }
        tvChangePhoto.setOnClickListener { imagePicker.launch("image/*") }

        btnSave.setOnClickListener { saveProfile() }
    }

    private fun loadUserData() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val name = snapshot.child("name")
                    .getValue(String::class.java)

                val imageUrl = snapshot.child("profileImage")
                    .getValue(String::class.java)

                if (!name.isNullOrEmpty()) {
                    etName.hint = name
                }

                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(this@activity_edit_profile)
                        .load(imageUrl)
                        .into(imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun saveProfile() {
        val name = etName.text.toString().trim()

        if (name.isNotEmpty()) {
            userRef.child("name").setValue(name)
        }

        if (selectedImageUri != null) {
            uploadImageToCloudinary(selectedImageUri!!)
        } else {
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun uploadImageToCloudinary(uri: Uri) {
        MediaManager.get()
            .upload(uri)
            .unsigned("images")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String) {
                    Toast.makeText(
                        this@activity_edit_profile,
                        "Uploading image...",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onSuccess(
                    requestId: String,
                    resultData: Map<*, *>
                ) {
                    val imageUrl = resultData["secure_url"].toString()

                    userRef.child("profileImage")
                        .setValue(imageUrl)

                    Toast.makeText(
                        this@activity_edit_profile,
                        "Profile updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()
                }

                override fun onError(
                    requestId: String,
                    error: com.cloudinary.android.callback.ErrorInfo
                ) {
                    Toast.makeText(
                        this@activity_edit_profile,
                        "Upload failed: ${error.description}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onProgress(
                    requestId: String,
                    bytes: Long,
                    totalBytes: Long
                ) {}

                override fun onReschedule(
                    requestId: String,
                    error: com.cloudinary.android.callback.ErrorInfo
                ) {}
            })
            .dispatch()
    }
}