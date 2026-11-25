import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

abstract class GenerateSupabaseConfig : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val localPropertiesFile: RegularFileProperty

    @get:OutputDirectory
    abstract val generatedDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val properties = Properties()
        val inputFile = localPropertiesFile.get().asFile
        require(inputFile.exists()) {
            "local.properties was not found."
        }
        inputFile.inputStream().use(properties::load)

        val url = requireNotNull(properties.getProperty("SUPABASE_URL")) {
            "SUPABASE_URL is missing from local.properties."
        }
        val key = requireNotNull(properties.getProperty("SUPABASE_PUBLISHABLE_KEY")) {
            "SUPABASE_PUBLISHABLE_KEY is missing from local.properties."
        }

        fun escape(value: String): String =
            value.replace("\\", "\\\\").replace("\"", "\\\"")

        val outputFile = generatedDirectory.get()
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

val generateSupabaseConfig = tasks.register<GenerateSupabaseConfig>("generateSupabaseConfig") {
    localPropertiesFile.set(rootProject.layout.projectDirectory.file("local.properties"))
    generatedDirectory.set(generatedSupabaseDir)
}

tasks.configureEach {
    if (name.startsWith("compile")) {
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
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.supabase.compose.auth)
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
