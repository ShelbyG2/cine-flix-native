import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    // Optional, provides the @Serialize annotation for autogeneration of Serializers.
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
val localProps = Properties()
val file = file("local.properties")
android {
    namespace = "tech.unrealistic.cineflix"
    compileSdk = 37


    defaultConfig {
        applicationId = "tech.unrealistic.cineflix"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
    // Material3 Icon catalog
    implementation("androidx.compose.material:material-icons-extended")

    // Core network client (Updated from 2.9.0)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

// Official serialization converter (Replaces JakeWharton's library)
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

// Core JSON engine
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    // Provides the AsyncImage composable for Jetpack Compose
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")

// Required by Coil 3 to download online movie posters (e.g. from TMDb URLs)
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    //network logger
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //color palette extractoir
    implementation("androidx.palette:palette-ktx:1.0.0")
}

