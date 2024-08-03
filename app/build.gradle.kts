plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("com.chaquo.python")
}

android {
    namespace = "com.evosticlabs.apollo"
    compileSdk = 34
    flavorDimensions += "pyVersion"
    defaultConfig {
        applicationId = "com.evosticlabs.apollo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            // On Apple silicon, you can omit x86_64.
            abiFilters += listOf("arm64-v8a", "x86_64")
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
    productFlavors {
        create("py310") { dimension = "pyVersion" }
        create("py311") { dimension = "pyVersion" }
    }
}

chaquopy {
    defaultConfig {
        pip {
//             A requirement specifier, with or without a version number:
            install("pandas")
            install("pydantic<2")
            install("openai")
            install("scikit-learn")
            install("pickle-mixin")
            // An sdist or wheel filename, relative to the project directory:
//            install("MyPackage-1.2.3-py2.py3-none-any.whl")

            // A directory containing a setup.py, relative to the project
            // directory (must contain at least one slash):
//            install("./python")

            // "-r"` followed by a requirements filename, relative to the
            // project directory:
//            install("-r", "requirements.txt")
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")


    //compose navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    // Material Design 3
    implementation(libs.material3)
    implementation(libs.androidx.material)
    // or skip Material Design and build directly on top of foundational components
    implementation(libs.androidx.foundation)

    //Places API
    implementation(libs.play.services.location)
    implementation(libs.places)

    //compose maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    //EasyPrefs
    implementation(libs.easyprefs)

    //lottie
    implementation("com.airbnb.android:lottie:5.2.0")

    //MPChart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

}