package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.caffeeborcelle.MyAdapter.SearchAdapter
import com.example.caffeeborcelle.databinding.FragmentSearchBinding
import com.example.caffeeborcelle.model.ProductEntity
import com.example.caffeeborcelle.activity_manage_product_details
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.*


class fragment_search : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SearchAdapter

    // ☕ Firebase coffee node
    private val coffeeRef =
        FirebaseDatabase.getInstance().getReference("products")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchAdapter(
            emptyList(),
            onItemClick = { coffee ->
                startActivity(
                    Intent(requireContext(), activity_manage_product_details::class.java).apply {
                        putExtra("productId", coffee.id)
                    }
                )
            },
            onAddToCartClick = { coffee ->
                addToCart(coffee)
            }
        )

        binding.SearchRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.SearchRecyclerView.adapter = adapter

        binding.SearchView.isIconified = false
        binding.SearchView.clearFocus()

        loadCoffee("")

        binding.SearchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    loadCoffee(query ?: "")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    loadCoffee(newText ?: "")
                    return true
                }
            }
        )
    }

    // ☕ Load & filter coffee
    private fun loadCoffee(query: String) {

        coffeeRef.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val result = mutableListOf<ProductEntity>()

                    for (snap in snapshot.children) {
                        val coffee =
                            snap.getValue(ProductEntity::class.java)

                        if (coffee != null &&
                            (query.isBlank() ||
                                    coffee.name.contains(query, true))
                        ) {
                            result.add(coffee)
                        }
                    }

                    if (result.isEmpty()) {
                        binding.EmptyStateLayout.visibility = View.VISIBLE
                        binding.SearchRecyclerView.visibility = View.GONE
                    } else {
                        binding.EmptyStateLayout.visibility = View.GONE
                        binding.SearchRecyclerView.visibility = View.VISIBLE
                        adapter.updateList(result)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.EmptyStateLayout.visibility = View.VISIBLE
                }
            }
        )
    }

    // ☕ Add to cart
    private fun addToCart(coffee: ProductEntity) {

        val email = SessionManager(requireContext()).getUserEmail()

        if (email.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please login first",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val emailKey = email.replace(".", "_")

        val cartRef = FirebaseDatabase.getInstance()
            .getReference("coffee_carts")
            .child(emailKey)
            .child(coffee.id)

        cartRef.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val currentQty =
                        snapshot.child("quantity")
                            .getValue(Int::class.java) ?: 0

                    val cartData = mapOf(
                        "productId" to coffee.id,
                        "productName" to coffee.name,
                        "productImage" to coffee.imageUrl,
                        "price" to coffee.price,
                        "quantity" to currentQty + 1
                    )

                    cartRef.setValue(cartData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                if (currentQty == 0)
                                    "Added to cart"
                                else
                                    "Added to cart",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
