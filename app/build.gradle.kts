plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("io.realm.kotlin")
}

android {
    namespace = "com.mauleCo.cornerpocket"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mauleCo.cornerpocket"
        minSdk = 26
        targetSdk = 34
        versionCode = 4
        versionName = "1.2.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    //Ad integration
    implementation("com.google.android.gms:play-services-ads:23.2.0")

    // Image cropping
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    //PDF viewer
//    implementation("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
//    implementation("com.github.barteksc:android-pdf-viewer:2.8.2")
    implementation("com.github.mhiew:android-pdf-viewer:3.2.0-beta.1")
//    implementation("com.github.barteksc:android-pdf-viewer:2.8.0")
//    implementation("com.github.bartekpdfium:android-pdfium:1.9.0")

    implementation("io.realm.kotlin:library-base:1.11.0")

    // In app reviews
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}