package com.example.caffeeborcelle.AdminFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import com.example.caffeeborcelle.activity_admin_user_detail
import com.example.caffeeborcelle.AdminAdapter.UserInfoAdapter
import com.example.caffeeborcelle.databinding.FragmentUserManagementBinding
import com.example.caffeeborcelle.viewmodel.UserViewModel


class UserManagementFragment : Fragment() {

    private var _binding: FragmentUserManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserInfoAdapter
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup
        adapter = UserInfoAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // ViewModel setup
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Observe users
        userViewModel.allUsers.observe(viewLifecycleOwner) { users ->
            if (users.isEmpty()) {
                Log.d("CoffeeUserManagement", "No users found")
            } else {
                Log.d("CoffeeUserManagement", "Users found: ${users.size}")
            }
            adapter.submitList(users)
        }

        // Click listener
        adapter.onUserClick = { user ->
            val intent = Intent(requireContext(), activity_admin_user_detail::class.java).apply {
                putExtra("email", user.email)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}