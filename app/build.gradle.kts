plugins {
    kotlin("kapt")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")

    jacoco
}

apply("jacoco-report.gradle.kts")

android {
    namespace = "com.example.mytodoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mytodoapp"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.mytodoapp.testhelpers.hilt.HiltTestRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    testCoverage {
        jacocoVersion = "0.8.12"
    }

    testOptions {
        animationsDisabled = true
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

kapt {
    correctErrorTypes = true
}

dependencies {
    // Base
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Activity
    implementation("androidx.activity:activity-ktx:1.9.0")

    // Fragment
    val fragmentVersion = "1.7.1"
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.1")

    // Shared Preferences
    implementation("androidx.preference:preference-ktx:1.2.1")

    // Navigation
    val navVersion = "2.7.7"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Dependency Injection with Dagger Hilt
    val hiltVersion = "2.51"
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    // kapt because ksp is alpha
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // Room Database
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    // Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$roomVersion")

    // Tests

    // Base tests
    testImplementation("junit:junit:4.13.2")
    //// Core library
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    //// AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    //// Assertions
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    //// Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Testing fragments
    debugImplementation("androidx.fragment:fragment-testing:$fragmentVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    // Testing Room
    testImplementation("androidx.room:room-testing:$roomVersion")

    // Testing with Hilt
    //// For instrumented tests.
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    //// ...with Kotlin.
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")

    // UI Automator
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0")

    // Jacoco
    androidTestImplementation("org.jacoco:org.jacoco.core:0.8.12")
}