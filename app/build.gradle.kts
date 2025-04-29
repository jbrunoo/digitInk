import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
//    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    load(FileInputStream(file))
}
val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    load(FileInputStream(file))
}

fun getProperty(property: String): String {
    return gradleLocalProperties(rootDir, providers).getProperty(property)
}

android {
    namespace = "com.jbrunoo.digitink"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jbrunoo.digitink"
        minSdk = 24
        targetSdk = 35
        versionCode = 13
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        setProperty(
            "archivesBaseName",
            "digitInk-$versionName"
        )

        manifestPlaceholders["ADMOB_ID"] = getProperty("ADMOB_ID")
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BANNER_AD_ID", "\"ca-app-pub-3940256099942544/9214589741\"")
            buildConfigField("String", "REWARD_AD_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
            signingConfig = signingConfigs.getByName("release")

            buildConfigField("String", "BANNER_AD_ID", getProperty("BANNER_AD_ID"))
            buildConfigField("String", "REWARD_AD_ID", getProperty("REWARD_AD_ID"))
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
        mlModelBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui:1.8.0")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("org.tensorflow:tensorflow-lite-support:0.3.1")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // viewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // navigation
    implementation("androidx.navigation:navigation-compose:2.8.9")
    // hilt
    implementation("com.google.dagger:hilt-android:2.55")
    // ksp("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    // preferences dataStore
    implementation("androidx.datastore:datastore-preferences:1.1.4")
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    // gms
    implementation("com.google.android.gms:play-services-games-v2:20.1.2")
    // logger
    implementation("com.jakewharton.timber:timber:5.0.1")
    // startUp
    implementation("androidx.startup:startup-runtime:1.2.0")
    // ads
    implementation("com.google.android.gms:play-services-ads:24.2.0")
}
