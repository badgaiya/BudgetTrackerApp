// settings.gradle.kts
pluginManagement {
    repositories {
        google() // ✅ Required for Android Gradle Plugin
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()       // ✅ Required for AndroidX and AGP dependencies
        mavenCentral()
        maven { url = uri("https://jitpack.io") } // ✅ Optional but useful for GitHub libs
    }
}

rootProject.name = "BudgetTracker"
include(":app")// Includes your app module