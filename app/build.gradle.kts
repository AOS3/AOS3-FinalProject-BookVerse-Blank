import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(project.rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.blank.bookverse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.blank.bookverse"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders += mapOf(
            "KAKAO_NATIVE_APP_KEY" to properties.getProperty("KAKAO_NATIVE_APP_KEY"),
            "GOOGLE_CLIENT_ID" to properties.getProperty("GOOGLE_CLIENT_ID"),
        )

        buildConfigField(
            "String",
            "KAKAO_NATIVE_APP_KEY",
            "\"${properties.getProperty("KAKAO_NATIVE_APP_KEY")}\""
        )
        buildConfigField(
            "String",
            "GOOGLE_CLIENT_ID",
            "\"${properties.getProperty("GOOGLE_CLIENT_ID")}\""
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Hilt
    implementation(libs.hilt.core)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    kspAndroidTest(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Sandwich
    implementation(libs.sandwich)
    implementation(libs.sandwich.retrofit)

    // Landscapist
    implementation(libs.landscapist.coil)

    // KakaoTalk
    implementation(libs.v2.all)

    // Paging3
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)

    // Coil
    implementation(libs.coil.compose)

    //Accompanist
    implementation(libs.accompanist.permissions)

    // Timber
    implementation(libs.timber)

    // FireBase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.functions.ktx)


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
}