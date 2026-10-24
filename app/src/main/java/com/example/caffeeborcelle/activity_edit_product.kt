package com.example.caffeeborcelle

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.caffeeborcelle.databinding.ActivityEditProductBinding
import com.example.caffeeborcelle.model.ProductEntity
import com.google.firebase.database.FirebaseDatabase

class activity_edit_product : AppCompatActivity() {

    private lateinit var binding: ActivityEditProductBinding
    private lateinit var productId: String
    private var product: ProductEntity? = null
    private var newImageUrl: String? = null

    // -------- IMAGE PICKER --------
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadImageToCloudinary(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productId = intent.getStringExtra("productId") ?: ""
        if (productId.isEmpty()) {
            Toast.makeText(this, "Invalid coffee product", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadCoffeeProduct()
        setupPriceCalculation()

        binding.EditProductImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.SaveBtn.setOnClickListener { saveCoffeeProduct() }
        binding.CancelBtn.setOnClickListener { finish() }
    }

    // -------- LOAD COFFEE --------
    private fun loadCoffeeProduct() {
        FirebaseDatabase.getInstance()
            .getReference("products") // 🔥 coffee node
            .child(productId)
            .get()
            .addOnSuccessListener {
                product = it.getValue(ProductEntity::class.java)
                product?.let { fillData(it) }
            }
    }

    private fun fillData(p: ProductEntity) = with(binding) {
        Glide.with(this@activity_edit_product)
            .load(p.imageUrl)
            .into(EditProductImage)

        EditProductImage.tag = "valid"

        EditProductName.setText(p.name)
        EditRating.rating = p.rating.toFloat()
        EditReviews.setText(p.reviews.toString())
        EditDiscount.setText(p.discount.toString())
        EditOriginalPrice.setText(p.originalPrice.toString())
        EditPrice.setText(p.price.toString())
       // EditShortDescription.setText(p.shortDesc)
        EditFullDescription.setText(p.fullDesc)
        //EditKeyFeatures.setText(p.keyFeatures)
    }

    // -------- CLOUDINARY --------
    private fun uploadImageToCloudinary(uri: Uri) {

        MediaManager.get().upload(uri)
            .unsigned("images")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String) {
                    Toast.makeText(this@activity_edit_product, "Uploading...", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    newImageUrl = resultData["secure_url"].toString()

                    Glide.with(this@activity_edit_product)
                        .load(newImageUrl)
                        .into(binding.EditProductImage)

                    binding.EditProductImage.tag = "valid"
                }

                override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    Toast.makeText(this@activity_edit_product, "Upload failed", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {}
            })
            .dispatch()
    }

    // -------- SAVE --------
    private fun saveCoffeeProduct() {

        if (binding.EditProductImage.tag != "valid") {
            Toast.makeText(this, "Select valid image", Toast.LENGTH_SHORT).show()
            return
        }

        val updated = product?.copy(
            imageUrl = newImageUrl ?: product!!.imageUrl,
            name = binding.EditProductName.text.toString(),
            rating = binding.EditRating.rating.toDouble(),
            reviews = binding.EditReviews.text.toString().toIntOrNull() ?: 0,
            discount = binding.EditDiscount.text.toString().toDoubleOrNull() ?: 0.0,
            originalPrice = binding.EditOriginalPrice.text.toString().toDoubleOrNull() ?: 0.0,
            price = binding.EditPrice.text.toString().toDoubleOrNull() ?: 0.0,
           // shortDesc = binding.EditShortDescription.text.toString(),
            fullDesc = binding.EditFullDescription.text.toString(),
            //keyFeatures = binding.EditKeyFeatures.text.toString()
        )

        FirebaseDatabase.getInstance()
            .getReference("products")
            .child(productId)
            .setValue(updated)
            .addOnSuccessListener {
                Toast.makeText(this, "Coffee updated", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    // -------- AUTO PRICE --------
    private fun setupPriceCalculation() {

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val discount = binding.EditDiscount.text.toString().toDoubleOrNull() ?: 0.0
                val original = binding.EditOriginalPrice.text.toString().toDoubleOrNull() ?: 0.0

                if (original > 0 && discount in 0.0..100.0) {
                    val finalPrice = original * (1 - discount / 100)
                    binding.EditPrice.setText("%.2f".format(finalPrice))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.EditDiscount.addTextChangedListener(watcher)
        binding.EditOriginalPrice.addTextChangedListener(watcher)

        binding.EditPrice.isEnabled = false
    }
}