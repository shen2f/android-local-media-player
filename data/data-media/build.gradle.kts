plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.shen.mediaplayer.data.media"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":core:core-common"))
    implementation(project(":core:core-domain"))
    implementation(project(":utils:utils-storage"))
    
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:${rootProject.ext.get("hiltVersion")}")
    kapt("com.google.dagger:hilt-android-compiler:${rootProject.ext.get("hiltVersion")}")
    // JSR 330 inject annotations
    implementation("javax.inject:javax.inject:1")
}
