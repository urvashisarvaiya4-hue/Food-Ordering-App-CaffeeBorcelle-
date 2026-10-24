package com.example.caffeeborcelle.util

import android.content.Context

class SessionManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("COFFEE_USER_SESSION", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_EMAIL = "LOGGED_IN_EMAIL"
        private const val KEY_NAME = "LOGGED_IN_NAME"
        private const val KEY_IS_LOGGED_IN = "IS_LOGGED_IN"
    }

    // ✅ Create Login Session
    fun createLoginSession(email: String, name: String) {
        prefs.edit().apply {
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    // ✅ Get User Email
    fun getUserEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    // ✅ Get User Name
    fun getUserName(): String? {
        return prefs.getString(KEY_NAME, null)
    }

    // ✅ Check Login Status
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // ✅ Logout User
    fun logout() {
        prefs.edit().clear().apply()
    }
}