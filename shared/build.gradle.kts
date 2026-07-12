import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    jvm()
    
    js {
        browser()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    
    android {
       namespace = "com.iqbalfauzi.kitchenstock.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.icons.extended)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.jetbrains.navigation3.ui)

            // DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Database
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)

            // Backend
            implementation(libs.supabase.postgrest)
            implementation(libs.supabase.auth)
            implementation(libs.supabase.realtime)

            // Image Loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Networking & Debugging
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.datetime)
            implementation(libs.napier)
            implementation(libs.multiplatform.settings.core)
            implementation(libs.multiplatform.settings.noarg)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        iosMain.dependencies {
            implementation(libs.sqldelight.native.driver)
            implementation(libs.ktor.client.darwin)
        }
        jvmMain.dependencies {
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.ktor.client.okhttp)
        }
        wasmJsMain.dependencies {
            implementation(libs.sqldelight.wasm.driver)
            implementation(libs.ktor.client.js)
        }
        jsMain.dependencies {
            implementation(libs.wrappers.browser)
            implementation(libs.sqldelight.wasm.driver)
            implementation(libs.ktor.client.js)
        }
    }
}

sqldelight {
    databases {
        create("KitchenDatabase") {
            packageName.set("com.iqbalfauzi.kitchenstock.db")
        }
    }
}

buildkonfig {
    packageName = "com.iqbalfauzi.kitchenstock"

    defaultConfigs {
        val projectId = project.findProperty("supabase.project.id")?.toString() ?: ""
        val anonKey = project.findProperty("supabase.anon.key")?.toString() ?: ""
        
        buildConfigField(STRING, "SUPABASE_URL", "https://$projectId.supabase.co")
        buildConfigField(STRING, "SUPABASE_ANON_KEY", anonKey)
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}
