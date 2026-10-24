package com.example.caffeeborcelle

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.caffeeborcelle.databinding.FragmentUserAccountBinding
import com.example.caffeeborcelle.model.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class fragment_user_account : Fragment() {

    private var _binding: FragmentUserAccountBinding? = null
    private val binding get() = _binding!!

    private var userRef: DatabaseReference? = null
    private var ordersRef: DatabaseReference? = null
    private var wishlistRef: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentUserAccountBinding.inflate(inflater, container, false)

        setupClickListeners()
        checkUser()

        return binding.root
    }

    private fun setupClickListeners() {

        // ✏️ Edit Profile
        binding.EditProfile.setOnClickListener {
            startActivity(Intent(requireContext(), activity_edit_profile::class.java))
        }

        // 📦 Orders
        binding.Order.setOnClickListener {
            startActivity(Intent(requireContext(), activity_order_product_status::class.java))
        }

        // 🎁 Rewards
        binding.txtrewards.setOnClickListener {
            startActivity(Intent(requireContext(), RewardsActivity::class.java))
        }

        // 🆘 Help Center
        binding.txthelpcenter.setOnClickListener {
            startActivity(Intent(requireContext(), HelpCenterActivity::class.java))
        }

        // 🔒 Privacy Policy
        binding.txtprivacypolicy.setOnClickListener {
            startActivity(Intent(requireContext(), PrivacyPolicyActivity::class.java))
        }

        // 🚪 Logout
        binding.LogoutBtn.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun checkUser() {

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            showGuestUI()
            return
        }

        val uid = currentUser.uid

        userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)

        ordersRef = FirebaseDatabase.getInstance()
            .getReference("coffee_orders")
            .child(uid)

        wishlistRef = FirebaseDatabase.getInstance()
            .getReference("coffee_wishlist")
            .child(uid)

        observeUser()
        observeOrders()
        observeWishlist()
    }

    // 👤 Load User Data
    private fun observeUser() {
        userRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserEntity::class.java)

                if (user != null) {
                    binding.UserName.text = user.name ?: "User"
                    binding.UserEmail.text = user.email ?: ""

                    if (!user.profileImage.isNullOrEmpty()) {
                        Glide.with(this@fragment_user_account)
                            .load(user.profileImage)
                            .into(binding.ProfileImage)
                    } else {
                        binding.ProfileImage.setImageResource(R.drawable.account_icon)
                    }

                } else {
                    showGuestUI()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 📦 Orders Count
    private fun observeOrders() {
        ordersRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.OrdersCount.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ❤️ Wishlist Count
    private fun observeWishlist() {
        wishlistRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.WishlistCount.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 👤 Guest UI
    private fun showGuestUI() {
        binding.UserName.text = "Guest"
        binding.UserEmail.text = "Not Logged In"
        binding.OrdersCount.text = "0"
        binding.WishlistCount.text = "0"
        binding.ProfileImage.setImageResource(R.drawable.account_icon)
    }

    // 🚪 Logout Dialog
    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Do you want to logout?")
            .setPositiveButton("Logout") { _, _ ->

                FirebaseAuth.getInstance().signOut()

                startActivity(
                    Intent(requireContext(), activity_login_page::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}