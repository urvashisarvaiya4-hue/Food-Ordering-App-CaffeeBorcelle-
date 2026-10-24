package com.example.caffeeborcelle.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.FragmentCategoryProductsBinding
import com.example.caffeeborcelle.model.ProductEntity
import com.example.caffeeborcelle.MyAdapter.BestsellersAdapter
import com.google.firebase.database.*

class CategoryProductsFragment : Fragment() {

    private lateinit var binding: FragmentCategoryProductsBinding
    private lateinit var adapter: BestsellersAdapter
    private lateinit var database: DatabaseReference
    private var categoryListener: ValueEventListener? = null

    private var selectedCategory = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)

        selectedCategory = arguments?.getString("category") ?: ""

        // Toolbar title
        //requireActivity()
          //  .findViewById<TextView>(R.id.toolbar_title)
            //?.text = selectedCategory

        setupRecyclerView()
        loadCategoryProducts()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = BestsellersAdapter(requireContext())
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }

    private fun loadCategoryProducts() {

        database = FirebaseDatabase.getInstance().getReference("products")

        categoryListener = database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                val list = ArrayList<ProductEntity>()

                Log.d("CategoryDebug", "Selected Category = $selectedCategory")

                for (snap in snapshot.children) {

                    val product = snap.getValue(ProductEntity::class.java)

                    product?.let {

                        it.id = snap.key ?: ""

                        Log.d("CategoryDebug", "Firebase Product Category = ${it.category}")

                        // 🔥 SAFE CATEGORY MATCH
                        val firebaseCategory = it.category
                            ?.replace("-", "")
                            ?.replace(" ", "")
                            ?.trim()
                            ?.lowercase()

                        val userCategory = selectedCategory
                            .replace("-", "")
                            .replace(" ", "")
                            .trim()
                            .lowercase()

                        if (firebaseCategory == userCategory) {
                            list.add(it)
                        }
                    }
                }

                Log.d("CategoryDebug", "Total Products Found = ${list.size}")

                adapter.submitList(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CategoryDebug", "Firebase error: ${error.message}")
            }
        })
    }
    override fun onResume() {
        super.onResume()

        requireActivity()
            .findViewById<TextView>(R.id.toolbar_title)
            ?.text = selectedCategory
    }

    override fun onDestroyView() {
        super.onDestroyView()
        categoryListener?.let {
            database.removeEventListener(it)
        }
    }
}