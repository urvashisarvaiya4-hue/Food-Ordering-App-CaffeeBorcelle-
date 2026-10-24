package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.caffeeborcelle.MyAdapter.WishlistAdapter
import com.example.caffeeborcelle.databinding.FragmentWishlistBinding
import com.example.caffeeborcelle.model.WishlistEntity
import com.example.caffeeborcelle.util.SessionManager
import com.google.firebase.database.*


class fragment_wishlist : Fragment() {

        private var _binding: FragmentWishlistBinding? = null
        private val binding get() = _binding!!

        private lateinit var adapter: WishlistAdapter
        private val wishlistList = mutableListOf<WishlistEntity>()

        private lateinit var session: SessionManager
        private lateinit var wishlistRef: DatabaseReference

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentWishlistBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            session = SessionManager(requireContext())

            adapter = WishlistAdapter(
                list = wishlistList,
                onItemClick = { item ->
                    startActivity(
                        Intent(requireContext(), activity_manage_product_details::class.java).apply {
                            putExtra("productId", item.id)
                        }
                    )
                },
                onRemoveClick = { item ->
                    removeFromWishlist(item)
                }
            )

            binding.wishlistRecyclerView.layoutManager =
                GridLayoutManager(requireContext(), 2)

            binding.wishlistRecyclerView.adapter = adapter

            loadWishlist()
        }

        private fun loadWishlist() {

            val email = session.getUserEmail() ?: return
            val emailKey = email.replace(".", "_")

            wishlistRef = FirebaseDatabase.getInstance()
                .getReference("coffee_wishlists")
                .child(emailKey)

            wishlistRef.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    wishlistList.clear()

                    for (snap in snapshot.children) {
                        val item = snap.getValue(WishlistEntity::class.java)
                        if (item != null) wishlistList.add(item)
                    }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        private fun removeFromWishlist(item: WishlistEntity) {

            val email = session.getUserEmail() ?: return
            val emailKey = email.replace(".", "_")

            FirebaseDatabase.getInstance()
                .getReference("coffee_wishlists")
                .child(emailKey)
                .child(item.id)
                .removeValue()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

