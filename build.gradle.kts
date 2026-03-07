// Top-level build file where you can add configuration options common to all sub-projects.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("org.jetbrains.kotlin.kapt") version "1.9.20" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("androidx.room") version "2.6.1" apply false
}

ext {
    // Core versions
    val kotlinVersion = "1.9.20"
    val agpVersion = "8.2.0"
    val compileSdk = 34
    val minSdk = 26
    val targetSdk = 34
    
    // Jetpack versions
    val coreKtxVersion = "1.12.0"
    val lifecycleVersion = "2.6.2"
    val activityComposeVersion = "1.8.2"
    val composeBomVersion = "2023.10.01"
    
    // Third party versions
    val hiltVersion = "2.48"
    val roomVersion = "2.6.1"
    val exoPlayerVersion = "1.2.0"
    val coilVersion = "2.5.0"
    val accompanistVersion = "0.32.0"
    
    // Test versions
    val junitVersion = "5.10.0"
    val mockkVersion = "1.13.8"
    val androidTestJunitVersion = "1.1.5"
    val espressoVersion = "3.5.1"
}
