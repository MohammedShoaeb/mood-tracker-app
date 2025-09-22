import org.gradle.kotlin.dsl.implementation

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.shadow.moodtracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shadow.moodtracker"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX core & lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // Compose BOM and UI libraries
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.text.google.fonts)
    // Navigation & runtime livedata
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    // Firebase BOM and Firebase libs (use BOM once only)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.runtime)
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.lottie.compose)

}

// icons
dependencies {
    // AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    // Jetpack Compose Bill of Materials (BOM) and UI Components
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui) // Core UI elements
    implementation(libs.androidx.ui.graphics) // Graphics primitives for UI
    implementation(libs.androidx.ui.tooling.preview) // Tools for previewing Composables
    implementation(libs.androidx.material3) // Material Design 3 components
    implementation(libs.androidx.ui.text.google.fonts) // Support for Google Fonts in Compose Text
    // Navigation and LiveData Integration for Compose
    implementation(libs.androidx.navigation.runtime.ktx) // Core navigation runtime
    implementation(libs.androidx.navigation.compose) // Navigation integration for Compose
    implementation(libs.androidx.runtime.livedata) // LiveData integration for Compose runtime
    // Firebase Bill of Materials (BOM) and SDKs
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx) // Firebase Authentication services
    implementation(libs.firebase.firestore.ktx) // Firebase Cloud Firestore database
    implementation(libs.androidx.runtime) // AndroidX runtime components (general utility)

    // Local Libraries / Specific Feature Libraries
    implementation (libs.androidx.material.icons.extended) // Extended Material Design Icons
    implementation(libs.lottie.compose) // Lottie animations for Compose
    // Testing Dependencies
    testImplementation(libs.junit) // JUnit for local unit tests
    androidTestImplementation(libs.androidx.junit) // JUnit for Android instrumented tests
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing (AndroidX)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for test environment
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI test rules and APIs
    // Debugging Dependencies (only included in debug builds)
    debugImplementation(libs.androidx.ui.tooling) // Compose UI tooling for inspection
    debugImplementation(libs.androidx.ui.test.manifest) // Test manifest for UI tests
}

    //Hilt dep
   // classpath("com.google.dagger:hilt-android-gradle-plugin:$hilt_version")

