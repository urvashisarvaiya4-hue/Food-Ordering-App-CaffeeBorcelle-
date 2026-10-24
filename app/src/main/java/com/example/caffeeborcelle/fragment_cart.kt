package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caffeeborcelle.databinding.FragmentCartBinding
import com.example.caffeeborcelle.activity_checkout
import com.example.caffeeborcelle.activity_home_page
import com.example.caffeeborcelle.MyAdapter.CartAdapter
import com.example.caffeeborcelle.model.CartEntity
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.*

class fragment_cart : Fragment(), CartAdapter.OnCartItemActionListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CartAdapter
    private lateinit var cartRef: DatabaseReference
    private var cartListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCartBinding.inflate(inflater, container, false)

        val email = SessionManager(requireContext()).getUserEmail()

        if (email == null) {
            showEmptyCart()
            return binding.root
        }

        val emailKey = email.replace(".", "_")

        // 🔥 Firebase coffee carts node
        cartRef = FirebaseDatabase.getInstance()
            .getReference("coffee_carts")
            .child(emailKey)

        adapter = CartAdapter(mutableListOf(), this)

        binding.CartRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.CartRecyclerView.adapter = adapter

        observeCart()

        binding.StartShoppingButton.setOnClickListener {
            startActivity(Intent(requireContext(), activity_home_page::class.java))
            requireActivity().finish()
        }

        binding.ProceedButton.setOnClickListener {
            startActivity(Intent(requireContext(), activity_checkout::class.java))
        }

        return binding.root
    }

    // 🔥 Observe cart realtime
    private fun observeCart() {

        cartListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val items = mutableListOf<CartEntity>()

                for (child in snapshot.children) {

                    val map = child.value as? HashMap<*, *> ?: continue

                    val item = CartEntity(
                        productId = map["productId"] as? String,
                        productName = map["productName"] as? String,
                        productImage = map["productImage"] as? String,

                        price = when (val p = map["price"]) {
                            is Long -> p
                            is Double -> p.toLong()   // 🔥 ADD THIS LINE
                            is String -> p.toDoubleOrNull()?.toLong() ?: 0L
                            else -> 0L
                        },

                        quantity = when (val q = map["quantity"]) {
                            is Long -> q
                            is String -> q.toLongOrNull() ?: 1L
                            else -> 1L
                        }
                    )

                    items.add(item)
                }

                if (items.isEmpty()) showEmptyCart()
                else showCart(items)
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        cartRef.addValueEventListener(cartListener!!)
    }

    private fun showEmptyCart() {
        binding.CartRecyclerView.visibility = View.GONE
        binding.EmptyCartLayout.visibility = View.VISIBLE
        binding.ProceedButton.visibility = View.GONE
    }

    private fun showCart(items: List<CartEntity>) {

        binding.CartRecyclerView.visibility = View.VISIBLE
        binding.EmptyCartLayout.visibility = View.GONE

        adapter.setData(items.toMutableList())

        val total = items.sumOf {
            (it.price ?: 0L) * (it.quantity ?: 1L)
        }

        binding.ProceedButton.visibility = View.VISIBLE
        binding.ProceedButton.text = "PROCEED (₹$total)"
    }

    override fun onCartItemDeleted(cartItem: CartEntity, position: Int) {
        cartRef.child(cartItem.productId ?: "").removeValue()
    }

    override fun onQuantityChanged(
        cartItem: CartEntity,
        position: Int,
        newQuantity: Long
    ) {
        cartRef.child(cartItem.productId ?: "")
            .child("quantity")
            .setValue(newQuantity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cartListener?.let { cartRef.removeEventListener(it) }
        _binding = null
    }
}