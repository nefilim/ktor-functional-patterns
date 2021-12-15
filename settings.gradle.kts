rootProject.name = "ktor functional patterns"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
    "server",
)
