// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}
plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    kotlin("kapt") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.47" apply false
    kotlin("plugin.serialization") version "1.9.10" apply false
}
