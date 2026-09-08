pluginManagement {
    repositories {
        google()   // 🔥 remove filtering
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")   // 👈 ImageSlider mate compulsory
    }
}

rootProject.name = "CaffeeBorcelle"
include(":app")