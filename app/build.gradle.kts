plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")

}


android {
    namespace = "com.example.caffeeborcelle"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.caffeeborcelle"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


   
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    //implementation("com.google.firebase:firebase-database-ktx")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    // Firebase Realtime Database (Kotlin extensions)
    implementation("com.google.firebase:firebase-database-ktx")


    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    implementation("com.cloudinary:cloudinary-android:2.3.1")
    //firebase
    //implementation(platform("com.google.firebase:firebase-bom:34.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}