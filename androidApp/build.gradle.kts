import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

fun readProperties(propertiesFile: File): Properties {
    val properties = Properties()
    propertiesFile.inputStream().use { fis ->
        properties.load(fis)
    }
    return properties
}

val appPropertiesFile = File("./local.properties")
val appProperties = readProperties(appPropertiesFile)

val openAiApiKey = appProperties.getProperty("openAiApiKey")
val openAiOrganizationId = appProperties.getProperty("openAiOrganizationId")

android {
    namespace = "com.grappim.aipal.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.grappim.aipal.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "openAiApiKey",
            "\"$openAiApiKey\"",
        )
        buildConfigField(
            "String",
            "openAiOrganizationId",
            "\"$openAiOrganizationId\"",
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(platform(libs.openai.client.bom))
    implementation(libs.openai.client)
    runtimeOnly(libs.ktor.client.okhttp)

    implementation(platform(libs.koinBom))
    implementation(libs.koinCore)
    implementation(libs.koinAndroid)
    implementation(libs.koinCompose)

    implementation(libs.voskAndroid)
    implementation(libs.accompanist.permissions)

    implementation(libs.tts)
    implementation(libs.tts.compose)
    implementation(libs.androidx.navigation.compose)
}
