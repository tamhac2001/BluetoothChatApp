plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.example.bluetoothchatapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bluetoothchatapp"
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildToolsVersion = "34.0.0"
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(platform(libs.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    implementation("androidx.compose.material3:material3")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation("androidx.compose.ui:ui-tooling")

    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Optional - Integration with activities
    implementation(libs.activity.compose)
    // Optional - Integration with ViewModels
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // to use java.time api in lower sdk version
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //kotlin-related
    implementation(libs.kotlinx.serialization.json)

    //navigation
    implementation(libs.androidx.navigation.ui.ktx)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    kapt(libs.androidx.lifecycle.compiler)

    //dagger hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.compiler)

    val composeBom = platform("androidx.compose:compose-bom:2023.08.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)


}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}