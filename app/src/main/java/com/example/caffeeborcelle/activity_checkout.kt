package com.example.caffeeborcelle

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.caffeeborcelle.model.CartEntity
import com.example.caffeeborcelle.model.UserEntity
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class activity_checkout : AppCompatActivity() {

    private lateinit var fullNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var contactEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var stateEditText: EditText
    private lateinit var pincodeEditText: EditText
    private lateinit var paymentRadioGroup: RadioGroup
    private lateinit var placeOrderButton: Button

    private lateinit var userRef: DatabaseReference
    private lateinit var cartRef: DatabaseReference
    private lateinit var ordersRef: DatabaseReference

    private var loggedInEmail: String? = null
    private var loggedInName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initViews()
        loadUserData()
        setupPlaceOrder()
    }

    private fun initViews() {
        fullNameTextView = findViewById(R.id.fullNameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        contactEditText = findViewById(R.id.contactEditText)
        addressEditText = findViewById(R.id.addressEditText)
        cityEditText = findViewById(R.id.cityEditText)
        stateEditText = findViewById(R.id.stateEditText)
        pincodeEditText = findViewById(R.id.pincodeEditText)
        paymentRadioGroup = findViewById(R.id.paymentRadioGroup)
        placeOrderButton = findViewById(R.id.placeOrderButton)
    }

    private fun loadUserData() {

        val session = SessionManager(this)
        loggedInEmail = session.getUserEmail()

        if (loggedInEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val emailKey = loggedInEmail!!.replace(".", "_")

        userRef = FirebaseDatabase.getInstance()
            .getReference("coffee_users")
            .child(emailKey)

        cartRef = FirebaseDatabase.getInstance()
            .getReference("coffee_carts")
            .child(emailKey)

        ordersRef = FirebaseDatabase.getInstance()
            .getReference("coffee_orders")
            .child(emailKey)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val user = snapshot.getValue(UserEntity::class.java)
                loggedInName = user?.name

                fullNameTextView.text = user?.name ?: ""
                emailTextView.text = user?.email ?: loggedInEmail
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupPlaceOrder() {

        placeOrderButton.setOnClickListener {

            // 🔥 Validate fields
            if (contactEditText.text.isEmpty() ||
                addressEditText.text.isEmpty() ||
                cityEditText.text.isEmpty()
            ) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 Payment method validation
            val selectedId = paymentRadioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentMethod =
                findViewById<RadioButton>(selectedId).text.toString()

            saveOrderToFirebase(paymentMethod)
        }
    }

    private fun saveOrderToFirebase(paymentMethod: String) {

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(this@activity_checkout, "Cart is empty", Toast.LENGTH_SHORT).show()
                    return
                }

                val cartItems = mutableListOf<CartEntity>()

                for (child in snapshot.children) {
                    val item = child.getValue(CartEntity::class.java)
                    if (item != null) cartItems.add(item)
                }

                if (cartItems.isEmpty()) return

                val totalPrice = cartItems.sumOf {
                    (it.price ?: 0L) * (it.quantity ?: 0L)
                }.toDouble()

                val productNames = cartItems.joinToString {
                    "${it.productName} × ${it.quantity}"
                }

                val orderId = ordersRef.push().key ?: return

                val orderMap = mapOf(
                    "orderId" to orderId,
                    "fullName" to loggedInName,
                    "email" to loggedInEmail,
                    "contact" to contactEditText.text.toString(),
                    "address" to addressEditText.text.toString(),
                    "city" to cityEditText.text.toString(),
                    "state" to stateEditText.text.toString(),
                    "pincode" to pincodeEditText.text.toString(),
                    "paymentMethod" to paymentMethod,
                    "orderStatus" to "Pending",
                    "orderDate" to getCurrentDate(),
                    "productNames" to productNames,
                    "totalPrice" to totalPrice
                )

                ordersRef.child(orderId)
                    .setValue(orderMap)
                    .addOnSuccessListener {

                        cartRef.removeValue()
                        showSuccessDialog(paymentMethod, totalPrice)
                    }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun showSuccessDialog(payment: String, total: Double) {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_order_success)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)

            val params = attributes
            params.width = WindowManager.LayoutParams.MATCH_PARENT
            params.gravity = Gravity.CENTER
            attributes = params
        }

        dialog.findViewById<TextView>(R.id.paymentText).text = "Payment: $payment"
        dialog.findViewById<TextView>(R.id.totalText).text = "Total: ₹$total"

        dialog.findViewById<Button>(R.id.okButton).setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
    }

    private fun getCurrentDate(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
}