import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
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

abstract class GenerateSupabaseConfigTask : DefaultTask() {
    @get:InputFile
    abstract val localPropertiesFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val properties = Properties()
        val propertiesFile = localPropertiesFile.get().asFile

        require(propertiesFile.exists()) {
            "local.properties was not found."
        }

        propertiesFile.inputStream().use {
            properties.load(it)
        }

        fun requiredProperty(name: String): String =
            properties.getProperty(name)
                ?.trim()
                ?.takeIf(String::isNotEmpty)
                ?: error("$name is missing or empty in local.properties.")

        val url = requiredProperty("SUPABASE_URL")
        val key = requiredProperty("SUPABASE_PUBLISHABLE_KEY")

        fun escape(value: String): String =
            value.replace("\\", "\\\\").replace("\"", "\\\"")

        val outputFile = outputDirectory.get()
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

val generateSupabaseConfig = tasks.register<GenerateSupabaseConfigTask>("generateSupabaseConfig") {
    localPropertiesFile.set(rootProject.layout.projectDirectory.file("local.properties"))
    outputDirectory.set(generatedSupabaseDir)
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
