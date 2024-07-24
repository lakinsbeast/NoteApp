plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "ru.tfk.mainscreen"
    compileSdk = 34

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    api(project(":feature:search"))
    api(project(":feature:settings"))

    // hilt
    api("com.google.dagger:hilt-android:2.51")
    api("androidx.compose.material3:material3-android:1.2.1")
    implementation(project(":feature:makenote"))
    ksp("com.google.dagger:hilt-compiler:2.51")
    api("androidx.hilt:hilt-work:1.2.0")
    api("androidx.hilt:hilt-navigation-compose:1.0.0")

    // navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // compose
//    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.material:material:1.3.0-rc01")
    implementation("androidx.compose.compiler:compiler:1.3.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.7")
    implementation("androidx.activity:activity-compose:1.6.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.7")
}
