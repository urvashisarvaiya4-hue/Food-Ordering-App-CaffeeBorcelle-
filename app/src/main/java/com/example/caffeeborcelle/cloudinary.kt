package com.example.caffeeborcelle

import android.app.Application
import com.cloudinary.android.MediaManager

class cloudinary : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = mapOf(
            "cloud_name" to "dpsvwu1ey",
            "api_key" to "564853132313331",
            "api_secret" to "api_secret"
        )

        MediaManager.init(this, config)
    }
}