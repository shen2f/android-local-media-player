plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.shen.mediaplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shen.mediaplayer"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core modules
    implementation(project(":core:core-common"))
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-navigation"))
    implementation(project(":core:core-domain"))
    implementation(project(":core:core-database"))
    
    // Feature modules
    implementation(project(":features:feature-splash"))
    implementation(project(":features:feature-home"))
    implementation(project(":features:feature-videolist"))
    implementation(project(":features:feature-audiolist"))
    implementation(project(":features:feature-imagelist"))
    implementation(project(":features:feature-folders"))
    implementation(project(":features:feature-videoplayer"))
    implementation(project(":features:feature-audioplayer"))
    implementation(project(":features:feature-imagebrowser"))
    implementation(project(":features:feature-playbackhistory"))
    implementation(project(":features:feature-playlist"))
    implementation(project(":features:feature-settings"))
    implementation(project(":features:feature-search"))
    
    // Data modules
    implementation(project(":data:data-local"))
    implementation(project(":data:data-repository"))
    implementation(project(":data:data-media"))
    
    // Media modules
    implementation(project(":media:media-decoder"))
    implementation(project(":media:media-player"))
    
    // Utils modules
    implementation(project(":utils:utils-permission"))
    implementation(project(":utils:utils-storage"))
    implementation(project(":utils:utils-image"))
    
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    
    // Test
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("io.mockk:mockk:1.13.8")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
