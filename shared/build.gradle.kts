import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // If you want to run shared.androidMain then uncomment application and comment library
//    alias(libs.plugins.androidApplication)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_1_8)
            }
        }
    }

//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "shared"
//        browser {
//            commonWebpackConfig {
//                outputFileName = "aipal.js"
//            }
//        }
//        binaries.executable()
//    }

//    js {
//        browser()
//        binaries.executable()
//    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.uuid)
            implementation(libs.voyager)
            api(libs.logging)

            api(libs.androidx.datastore.preferences.core)
            implementation(libs.kotlinx.atomicfu)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
//            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(compose.materialIconsExtended)

            implementation(project.dependencies.platform(libs.openai.client.bom))
            implementation(libs.openai.client)
            runtimeOnly(libs.ktor.client.okhttp)

            implementation(libs.ktor.client.core)

            implementation(libs.okio)

            // Temporarily added so that with ios the project can be built
            // https://github.com/cashapp/sqldelight/issues/4357#issuecomment-1839905700
            implementation(libs.stately.common)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.apache)
        }
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

android {
    namespace = "com.grappim.aipal"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    packaging.resources.excludes.apply {
        add("META-INF/AL2.0")
        add("META-INF/LGPL2.1")
        add("META-INF/DEPENDENCIES")
        add("META-INF/LICENSE.md")
        add("META-INF/LICENSE-notice.md")
        add("META-INF/DEPENDENCIES")
    }

    dependencies {
        coreLibraryDesugaring(libs.android.desugarJdkLibs)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "com.grappim.aipal"
            packageVersion = "1.0.0"
        }
    }
}
