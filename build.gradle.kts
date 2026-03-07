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
    extra["kotlinVersion"] = "1.9.20"
    extra["agpVersion"] = "8.2.0"
    extra["compileSdk"] = 34
    extra["minSdk"] = 26
    extra["targetSdk"] = 34
    
    // Jetpack versions
    extra["coreKtxVersion"] = "1.12.0"
    extra["lifecycleVersion"] = "2.6.2"
    extra["activityComposeVersion"] = "1.8.2"
    extra["composeBomVersion"] = "2023.10.01"
    
    // Third party versions
    extra["hiltVersion"] = "2.48"
    extra["roomVersion"] = "2.6.1"
    extra["exoPlayerVersion"] = "1.2.0"
    extra["coilVersion"] = "2.5.0"
    extra["accompanistVersion"] = "0.32.0"
    
    // Test versions
    extra["junitVersion"] = "5.10.0"
    extra["mockkVersion"] = "1.13.8"
    extra["androidTestJunitVersion"] = "1.1.5"
    extra["espressoVersion"] = "3.5.1"
}
