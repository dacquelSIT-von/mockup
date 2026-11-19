import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.sqldelight)
}

val generatedSupabaseDir =
    layout.buildDirectory.dir("generated/supabase/commonMain/kotlin")

val generateSupabaseConfig = tasks.register("generateSupabaseConfig") {
    val localPropertiesFile = rootProject.file("local.properties")

    inputs.file(localPropertiesFile)
    outputs.dir(generatedSupabaseDir)

    doLast {
        val properties = Properties()

        require(localPropertiesFile.exists()) {
            "local.properties was not found."
        }

        localPropertiesFile.inputStream().use {
            properties.load(it)
        }

        val url = requireNotNull(properties.getProperty("SUPABASE_URL")) {
            "SUPABASE_URL is missing from local.properties."
        }

        val key = requireNotNull(properties.getProperty("SUPABASE_PUBLISHABLE_KEY")) {
            "SUPABASE_PUBLISHABLE_KEY is missing from local.properties."
        }

        fun escape(value: String): String =
            value.replace("\\", "\\\\").replace("\"", "\\\"")

        val outputFile = generatedSupabaseDir.get()
            .file("com/example/mockup_mobapp/data/remote/SupabaseConfig.kt")
            .asFile

        outputFile.parentFile.mkdirs()

        outputFile.writeText(
            """
            package com.example.mockup_mobapp.data.remote

            object SupabaseConfig {
                const val URL = "${escape(url)}"
                const val PUBLISHABLE_KEY = "${escape(key)}"
            }
            """.trimIndent()
        )
    }
}

tasks.configureEach {
    if (name.startsWith("compile") && name.contains("Kotlin")) {
        dependsOn(generateSupabaseConfig)
    }
}


kotlin {
    jvm()

    android {
        namespace = "com.example.mockup_mobapp.shared"
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
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    sourceSets {
        commonMain {
            kotlin.srcDir(generatedSupabaseDir)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.compose.uiTooling)
        }
        commonMain.dependencies {
            implementation(libs.supabase.auth)
            implementation(libs.supabase.postgrest)
            implementation(libs.sqldelight.runtime)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        getByName("androidDeviceTest").dependencies {
            implementation(libs.androidx.testRunner)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.sqldelight.sqlite.driver)
        }
    }

}
sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.example.mockup_mobapp.data.local")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}