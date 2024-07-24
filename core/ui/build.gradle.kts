plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.tfk.ui"
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    // нужна моделька Note, но она есть только у database, потому что содержит зависимости от room из-за аннотации, надо придумать что-то или оставить как есть
    api(project(":core:database"))
    implementation(project(":core:utils"))

    api("androidx.compose.material:material:1.3.0-rc01")
    api("androidx.compose.compiler:compiler:1.3.2")
    api("androidx.compose.ui:ui-tooling-preview:1.3.0-rc01")
    api("androidx.activity:activity-compose:1.6.0")
    implementation("androidx.compose.material3:material3-android:1.2.1")
    implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")

//    implementation("androidx.core:core-ktx:1.13.1")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
