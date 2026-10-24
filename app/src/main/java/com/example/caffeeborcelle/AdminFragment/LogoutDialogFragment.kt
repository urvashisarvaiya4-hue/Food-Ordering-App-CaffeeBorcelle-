package com.example.caffeeborcelle.AdminFragment


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.caffeeborcelle.activity_registration_page
import com.google.firebase.auth.FirebaseAuth

class LogoutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Logout Confirmation")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->

                // ✅ Firebase logout
                FirebaseAuth.getInstance().signOut()

                // ✅ Go to LoginPage
                val intent = Intent(requireContext(), activity_registration_page::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}