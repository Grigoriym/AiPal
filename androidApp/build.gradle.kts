plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.grappim.aipal.android"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.grappim.aipal.android"
        testApplicationId = "com.grappim.aipal.android.test"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.3"

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86_64", "x86"))
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging.resources.excludes.apply {
        add("META-INF/AL2.0")
        add("META-INF/LGPL2.1")
        add("META-INF/DEPENDENCIES")
        add("META-INF/LICENSE.md")
        add("META-INF/LICENSE-notice.md")
        add("META-INF/DEPENDENCIES")
    }
    signingConfigs {
        create("release") {
            storeFile = file("../androidApp/aipal_key.jks")
            keyAlias = System.getenv("AIPAL_ALIAS_R")
            keyPassword = System.getenv("AIPAL_KEY_PASS_R")
            storePassword = System.getenv("AIPAL_STORE_PASS_R")
            enableV2Signing = true
            enableV3Signing = true
        }
    }
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"

            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.desugarJdkLibs)
    implementation(platform(libs.androidx.compose.bom))
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.openai.client.bom))
    implementation(libs.openai.client)
    runtimeOnly(libs.ktor.client.okhttp)

    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.androidx.compose)

    implementation("net.java.dev.jna:jna:5.14.0@aar")
    implementation(libs.voskAndroid) {
        exclude(group = "net.java.dev.jna", module = "jna")
    }

    implementation(libs.accompanist.permissions)

    implementation(libs.tts)
    implementation(libs.tts.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.prefs)

    implementation(libs.kotlinx.datetime)

    implementation(libs.android.driver)
}
