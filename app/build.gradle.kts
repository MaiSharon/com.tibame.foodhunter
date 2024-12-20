plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.map.serect)
    alias(libs.plugins.google.gms.googleServices)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.tibame.foodhunter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tibame.foodhunter"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            // dev env
            buildConfigField("String","BASE_URL", "\"\"")
            isMinifyEnabled = false
        }
        release {
            buildConfigField("String","BASE_URL", "http://10.0.2.2:8080/com.tibame.foodhunter_server/")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)


//    implementation(libs.androidx.credentials)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.material.icons.extended)


    implementation(libs.gson)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

    implementation(libs.accompanist.permissions)
    implementation(libs.coil.compose)
    implementation(libs.androidx.espresso.core)
    implementation(libs.play.services.location)
    implementation(libs.cloudinary.android)
    implementation(libs.androidx.benchmark.common)
    implementation(libs.generativeai)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}