import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version PluginVersions.Kotlin apply false // I'm just here for the grammar
    id(PluginIds.TaskTree) version PluginVersions.TaskTree
    id(PluginIds.TestLogger) version PluginVersions.TestLogger apply false
    id(PluginIds.DependencyUpdates) version PluginVersions.DependencyUpdates apply false
    id(PluginIds.Jib) version PluginVersions.Jib apply false
    id(PluginIds.Kover) version PluginVersions.Kover
    `maven-publish`
}

repositories {
    mavenCentral()
}

kover {
    isEnabled = true                        // false to disable instrumentation of all test tasks in all modules
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ) // change instrumentation agent and reporter
    intellijEngineVersion.set("1.0.634")    // change version of IntelliJ agent and reporter
    generateReportOnCheck.set(true)         // false to do not execute `koverReport` task before `check` task
}

allprojects {
    group = "io.github.nefilim.ktorpatterns"
    version = "0.0.1"

    apply(plugin = PluginIds.DependencyUpdates)

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_11.toString()
        targetCompatibility = sourceCompatibility
    }

    // https://github.com/ben-manes/gradle-versions-plugin/discussions/482
    tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
        revision = "release"
        // reject all non stable versions
        rejectVersionIf {
            isNonStable(candidate.version)
        }

        // disallow release candidates as upgradable versions from stable versions
        rejectVersionIf {
            isStable(currentVersion) && isNonStable(candidate.version)
        }
    }
}

subprojects {
    // https://kotlinlang.org/docs/reference/using-gradle.html#using-gradle-kotlin-dsl
    apply {
        plugin(PluginIds.Kotlin)
        plugin(PluginIds.TestLogger)
        plugin(PluginIds.Jib)
    }

    repositories {
        mavenLocal()
        mavenCentral()
    }
    
    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            jvmTarget = "11"
            languageVersion = "1.6"
            apiVersion = "1.6"
        }
    }

    configure<com.adarshr.gradle.testlogger.TestLoggerExtension> {
        theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
        showCauses = true
        slowThreshold = 1000
        showSummary = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        listOf(
            Libraries.KotlinReflect,
            Libraries.KotlinStdlibJdk8,
        ).map {
            implementation(it)
        }
    }
}

fun isNonStable(version: String): Boolean {
    val containsStableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val simpleSemverRegex = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)".toRegex()
    val isStable = containsStableKeyword || simpleSemverRegex.matches(version)
            && !listOf("alpha", "beta", "rc").any { version.toLowerCase().contains(it) } && !version.contains("M")
    return isStable.not()
}

fun isStable(version: String): Boolean = !isNonStable(version)
