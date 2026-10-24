package com.example.caffeeborcelle.AdminAdapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.model.UserEntity
import com.google.firebase.database.FirebaseDatabase

class UserInfoAdapter :
    ListAdapter<UserEntity, UserInfoAdapter.UserVH>(DiffCallback()) {

    var onUserClick: ((UserEntity) -> Unit)? = null

    inner class UserVH(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_management_user_info_blueprint, parent, false)
    ) {

        private val tvName: TextView = itemView.findViewById(R.id.Name)
        private val tvEmail: TextView = itemView.findViewById(R.id.Email)
        private val tvPassword: TextView = itemView.findViewById(R.id.Password)
        private val btnDelete: ImageView = itemView.findViewById(R.id.UserRemoveIcon)

        fun bind(user: UserEntity) {
            tvName.text = user.name ?: "Coffee User"
            tvEmail.text = user.email ?: ""
            tvPassword.text = user.password ?: ""

            itemView.setOnClickListener { onUserClick?.invoke(user) }

            btnDelete.setOnClickListener {

                val email = user.email?.trim()
                val uid = user.uid?.trim()

                if (email.isNullOrEmpty() && uid.isNullOrEmpty()) {
                    Toast.makeText(itemView.context, "User info missing", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                AlertDialog.Builder(itemView.context)
                    .setTitle("Delete Coffee User?")
                    .setMessage("Are you sure?\n\n${email ?: uid}")
                    .setPositiveButton("Delete") { _, _ ->
                        deleteUserFromFirebase(email ?: "", uid ?: "")
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

            }
        }

        private fun deleteUserFromFirebase(email: String, uid: String) {

            val db = FirebaseDatabase.getInstance().reference

            val emailKey = email.replace(".", "_")
            val emailKey2 = emailKey.replace("@", "_")

            val updates = hashMapOf<String, Any?>()

            // 🔥 delete from coffee users
            if (uid.isNotEmpty()) updates["coffee_users/$uid"] = null
            if (email.isNotEmpty()) {
                updates["coffee_users/$emailKey"] = null
                updates["coffee_users/$emailKey2"] = null
            }

            // 🔥 delete related coffee data
            if (uid.isNotEmpty()) {
                updates["coffee_cart/$uid"] = null
                updates["coffee_wishlist/$uid"] = null
                updates["coffee_orders/$uid"] = null
            }

            if (email.isNotEmpty()) {
                updates["coffee_cart/$emailKey"] = null
                updates["coffee_wishlist/$emailKey"] = null
                updates["coffee_orders/$emailKey"] = null

                updates["coffee_cart/$emailKey2"] = null
                updates["coffee_wishlist/$emailKey2"] = null
                updates["coffee_orders/$emailKey2"] = null
            }

            db.updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(itemView.context, "Coffee user deleted", Toast.LENGTH_SHORT).show()

                    val newList = currentList.toMutableList()
                    val index = newList.indexOfFirst {
                        it.email?.equals(email, true) == true ||
                                it.uid == uid
                    }

                    if (index != -1) {
                        newList.removeAt(index)
                        submitList(newList)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(itemView.context, "Delete failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH =
        UserVH(parent)

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<UserEntity>() {
        override fun areItemsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem.email == newItem.email
        }

        override fun areContentsTheSame(oldItem: UserEntity, newItem: UserEntity): Boolean {
            return oldItem == newItem
        }
    }
}