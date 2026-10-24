package com.example.caffeeborcelle.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.caffeeborcelle.model.AdminEntity
import com.google.firebase.database.*

class AdminViewModel (application: Application) : AndroidViewModel(application) {

    // 🔥 Coffee app admin node
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

        override fun onCancelled(error: DatabaseError) {}
    }

    init {
        // 🔥 Real-time admin list
        adminsRef.addValueEventListener(adminsListener)
    }

    // 🔥 Insert admin
    fun insert(admin: AdminEntity) {

        val id = adminsRef.push().key ?: return
        adminsRef.child(id).setValue(admin)
    }

    // 🔥 Update admin
    fun update(adminId: String, admin: AdminEntity) {
        adminsRef.child(adminId).setValue(admin)
    }

    // 🔥 Delete admin
    fun delete(adminId: String) {
        adminsRef.child(adminId).removeValue()
    }

    // 🔥 Admin login
    fun login(
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

    override fun onCleared() {
        super.onCleared()
        adminsRef.removeEventListener(adminsListener)
    }
}