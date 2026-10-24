package com.example.caffeeborcelle.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.caffeeborcelle.model.AdminEntity
import com.google.firebase.database.*

class AdminRepository {

    private val adminsRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("coffee_admins")

    private val _allAdmins = MutableLiveData<List<AdminEntity>>()
    val allAdmins: LiveData<List<AdminEntity>> = _allAdmins

    private val adminsListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {

            val list = mutableListOf<AdminEntity>()

            for (snap in snapshot.children) {
                val admin = snap.getValue(AdminEntity::class.java)
                if (admin != null) {
                    list.add(admin)
                }
            }

            _allAdmins.postValue(list)
        }

        override fun onCancelled(error: DatabaseError) {
            _allAdmins.postValue(emptyList())
        }
    }

    init {
        // 🔥 Real-time admin list
        adminsRef.addValueEventListener(adminsListener)
    }

    // 🔥 Add Admin
    fun insertAdmin(admin: AdminEntity) {
        val id =
            admin.id.takeIf { it != 0 }
                ?.toString()
                ?: adminsRef.push().key
                ?: return

        adminsRef.child(id).setValue(admin)
    }

    // 🔥 Update Admin
    fun updateAdmin(admin: AdminEntity) {
        adminsRef.child(admin.id.toString())
            .setValue(admin)
    }

    // 🔥 Delete Admin
    fun deleteAdmin(admin: AdminEntity) {
        adminsRef.child(admin.id.toString())
            .removeValue()
    }

    // 🔥 Login Admin
    fun loginAdmin(
        username: String,
        password: String,
        callback: (AdminEntity?) -> Unit
    ) {
        adminsRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (snap in snapshot.children) {
                    val admin = snap.getValue(AdminEntity::class.java)

                    if (admin != null &&
                        admin.username == username &&
                        admin.password == password
                    ) {
                        callback(admin)
                        return
                    }
                }

                callback(null)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }

    fun clear() {
        adminsRef.removeEventListener(adminsListener)
    }
}