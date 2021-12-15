plugins {
    kotlin(PluginIds.KotlinKapt) // must not have a version apparently
    kotlin(PluginIds.KotlinXSerialization) version PluginVersions.Kotlin
    id(PluginIds.SourceSets) version PluginVersions.SourceSets
    id("application")
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.cloud.tools:jib-layer-filter-extension-gradle:0.1.0") // TODO
    }
}

testSets {
    create("integrationTest") {
        dirName = "integration-test"
    }
}

tasks {
    "check" {
        dependsOn("integrationTest")
    }
    "integrationTest" {
        mustRunAfter("test")
        outputs.upToDateWhen { false } // force full run every time
    }
    test {
        extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
            isEnabled = true
            binaryReportFile.set(file("$buildDir/custom/result.bin"))
            includes = listOf("io\\.github\\.nefilim\\.*")
            //excludes = listOf("com\\.example\\.subpackage\\..*")
        }
    }
}

configure<com.adarshr.gradle.testlogger.TestLoggerExtension> {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.STANDARD
    showCauses = true
    slowThreshold = 1000
    showSummary = true
    showStandardStreams = true
}

dependencies {
    listOf(
        platform(Libraries.ArrowStack),
        Libraries.ArrowCore,
        Libraries.ArrowFx,
        Libraries.ArrowOptics,
        Libraries.Config4k,
        Libraries.ExposedCore,
        Libraries.ExposedJDBC,
        Libraries.ExposedJavaTime,
//        Libraries.FigureRetry,
        Libraries.FlywayCore,
        Libraries.HikariCP,
        Libraries.JMXMP,
        Libraries.KafkaClients,
        Libraries.KotlinLogging,
        Libraries.KotlinXCoRoutinesCore,
        Libraries.KotlinXCoRoutinesDebug,
        Libraries.KotlinXCoRoutinesJDK8,
        Libraries.KotlinXCoRoutinesReactive,
        Libraries.KotlinXCoRoutinesReactor,
        Libraries.KotlinXSerializationJSON,
        Libraries.KtorAuth,
        Libraries.KtorAuthJWT,
        Libraries.KtorClientCore,
        Libraries.KtorClientCIO,
        Libraries.KtorClientLogging,
        Libraries.KtorClientSerialization,
        Libraries.KtorFeatureFlyway,
        Libraries.KtorHTML,
        Libraries.KtorServerNetty,
        Libraries.KtorServerSession,
        Libraries.KtorSerialization,
        Libraries.KtorWebsockets,
        Libraries.Lettuce,
        Libraries.LogbackClassic,
        Libraries.Postgres,
        Libraries.ProjectReactorCore,
        Libraries.ProjectReactorKafka,
        Libraries.ProjectReactorKotlinExtensions,
        Libraries.SLF4JAPI,
    ).map {
        implementation(it)
    }
    
    kapt(Libraries.ArrowMeta)

    listOf(
        Libraries.Kotest,
        Libraries.KotestAssertions,
        Libraries.KotestAssertionsArrow,
        Libraries.KotestAssertionsKtor,
        Libraries.KotestTestContainers,
        Libraries.KtorServerTests,
        Libraries.Mockk,

        Libraries.TestContainers,
        Libraries.TestContainersJUnit,
        Libraries.TestContainersKafka,
        Libraries.TestContainersPostgres,
    ).map {
        testImplementation(it)
    }

    listOf(
        Libraries.KotestTestContainers,
        Libraries.TestContainers,
        Libraries.TestContainersJUnit,
        Libraries.TestContainersKafka,
        Libraries.TestContainersPostgres,
        Libraries.LogbackClassic,
    ).map {
        integrationTestImplementation(it)
    }
}

val serverMainClassName = "io.github.nefilim.ktorpatterns.MainKt"

application {
    mainClass.set(serverMainClassName)
}

jib {
    from {
        image = "openjdk:11-jdk"
    }
    to {
        // image is set by --image on gradle invocation
        tags = tags + containerTags(project)
    }

    pluginExtensions {
        pluginExtension {
            implementation = "com.google.cloud.tools.jib.gradle.extension.layerfilter.JibLayerFilterExtension"
            configuration(Action<com.google.cloud.tools.jib.gradle.extension.layerfilter.Configuration> {
                filters {
                    // Delete all conf files
                    filter {
                        glob = "/app/resources/application*conf"
                    }
                    // but retain the container conf file we'll need
                    filter {
                        glob = "/app/resources/application-container.conf"
                        toLayer = "app config"
                    }
                }
            })
        }
    }

    extraDirectories {
        permissions = mapOf(
            "/docker-entrypoint.sh" to "755"
        )
    }

    container {
//        creationTime = grgit.log().first().dateTime.format(DateTimeFormatter.ISO_DATE_TIME) // https://github.com/GoogleContainerTools/jib/blob/master/docs/faq.md#why-is-my-image-created-48-years-ago
        ports = listOf("8080")
        mainClass = serverMainClassName

        entrypoint = listOf(
            "./docker-entrypoint.sh"
        )
        jib.container.creationTime = "USE_CURRENT_TIMESTAMP"
    }
}
