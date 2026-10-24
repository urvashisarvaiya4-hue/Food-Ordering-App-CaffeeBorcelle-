package com.example.caffeeborcelle

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.databinding.ActivityManagingProductOptionsAddBinding
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class activity_managing_product_options_add : AppCompatActivity() {
    private lateinit var binding: ActivityManagingProductOptionsAddBinding
    private lateinit var categorySpinner: Spinner
    private var selectedCategory = ""

    private val database = FirebaseDatabase.getInstance().reference

    // 📷 Image picker
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadImageToCloudinary(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagingProductOptionsAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.PriceInput.isEnabled = false

        setupCategorySpinner()
        setupPriceCalculation()
        setupClicks()
    }

    // 🔥 CoffeeBorcelle Categories
    private fun setupCategorySpinner() {

        categorySpinner = binding.spinnerCategory

        val categories = listOf(
            "Select Category",
            "Pizza",
            "Burger",
            "Dessert",
            "Thikshake",
            "Tea-Coffee"
        )

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCategory = categories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    // 💰 Auto price calculation
    private fun setupPriceCalculation() {

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculatePrice()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.DiscountInput.addTextChangedListener(watcher)
        binding.OriginalPriceInput.addTextChangedListener(watcher)
    }

    private fun setupClicks() {

        binding.ImagePreview.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.saveProductButton.setOnClickListener {
            saveProductToFirebase()
        }
    }

    // ☁️ Cloudinary Upload
    private fun uploadImageToCloudinary(uri: Uri) {

        lifecycleScope.launch(Dispatchers.IO) {
            try {

                val inputStream = contentResolver.openInputStream(uri) ?: return@launch
                val imageBytes = inputStream.readBytes()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file",
                        "coffee.jpg",
                        RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
                    )
                    .addFormDataPart("upload_preset", "images")
                    .build()

                val request = Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dpsvwu1ey/image/upload")
                    .post(requestBody)
                    .build()

                val response = OkHttpClient().newCall(request).execute()
                val body = response.body?.string() ?: return@launch
                val imageUrl = org.json.JSONObject(body).getString("secure_url")

                withContext(Dispatchers.Main) {

                    Glide.with(this@activity_managing_product_options_add)
                        .load(imageUrl)
                        .into(binding.ImagePreview)

                    binding.ImageResInput.setText(imageUrl)
                    binding.ImagePreview.tag = "valid"

                    Toast.makeText(this@activity_managing_product_options_add, "Image uploaded", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@activity_managing_product_options_add, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 💸 Calculate price
    private fun calculatePrice() {

        val discount = binding.DiscountInput.text.toString().toDoubleOrNull()
        val original = binding.OriginalPriceInput.text.toString().toDoubleOrNull()

        if (discount == null || original == null) {
            binding.PriceInput.setText("")
            return
        }

        val price = original - (original * discount / 100)
        binding.PriceInput.setText("%.2f".format(price))
    }

    // 🔥 Save Coffee / Food item
    private fun saveProductToFirebase() {

        if (selectedCategory == "" || selectedCategory == "Select Category") {
            Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
            return
        }

        if (binding.ImagePreview.tag != "valid") {
            Toast.makeText(this, "Upload image first", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = database.child("products").push().key ?: return

        val productMap = mapOf(
            "id" to productId,
            "name" to binding.ProductNameInput.text.toString(),
            "imageUrl" to binding.ImageResInput.text.toString(),

            "price" to binding.PriceInput.text.toString().toDoubleOrNull(),
            "discount" to binding.DiscountInput.text.toString().toDoubleOrNull(),
            "originalPrice" to binding.OriginalPriceInput.text.toString().toDoubleOrNull(),
            "rating" to binding.ProductRatingInput.text.toString().toDoubleOrNull(),
            "reviews" to binding.ProductReviewsInput.text.toString().toIntOrNull(),

            "shortDesc" to binding.ShortDescriptionInput.text.toString(),
            //"fullDesc" to binding.FullDescriptionInput.text.toString(),
            //"keyFeatures" to binding.KeyFeaturesInput.text.toString(),
            "category" to selectedCategory,
            "time" to System.currentTimeMillis()
        )

        database.child("products").child(productId)
            .setValue(productMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Coffee item added", Toast.LENGTH_LONG).show()
                clearInputs()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun clearInputs() {
        binding.ProductNameInput.text?.clear()
        binding.ImageResInput.text?.clear()
        binding.PriceInput.text?.clear()
        binding.DiscountInput.text?.clear()
        binding.OriginalPriceInput.text?.clear()
        binding.ProductRatingInput.text?.clear()
        binding.ProductReviewsInput.text?.clear()
        binding.ShortDescriptionInput.text?.clear()
       // binding.FullDescriptionInput.text?.clear()
        //binding.KeyFeaturesInput.text?.clear()

        binding.ImagePreview.setImageResource(android.R.drawable.ic_menu_gallery)
        binding.ImagePreview.tag = "invalid"
    }
}