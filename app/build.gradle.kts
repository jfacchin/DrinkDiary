plugins {
    alias(libs.plugins.android.application)
    // ksp generates the room database code at build time
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.drinkdiary"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.drinkdiary"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // runs the database tests on a device or emulator
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // room: saves drinks in a local database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // viewmodel + livedata for mvvm
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    // lets fragments share the activity's viewmodel with "by activityViewModels()"
    implementation(libs.androidx.fragment.ktx)

    // coroutines run database work off the main thread
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
}
