object PluginIds { // please keep this sorted in sections
    // Kotlin
    const val Kotlin = "kotlin"
    const val KotlinKapt = "kapt"
    const val KotlinXSerialization = "plugin.serialization"

    // 3rd Party
    const val DependencyUpdates = "com.github.ben-manes.versions"
    const val Idea = "idea"
    const val Jib = "com.google.cloud.tools.jib"
    const val Kover = "org.jetbrains.kotlinx.kover"
    const val SourceSets = "org.unbroken-dome.test-sets"
    const val TaskTree = "com.dorongold.task-tree"
    const val TestLogger = "com.adarshr.test-logger"
}

object PluginVersions { // please keep this sorted in sections
    // Kotlin
    const val Kotlin = "1.6.0"

    // 3rd Party
    const val DependencyUpdates = "0.39.0"
    const val Flyway = "8.0.4"
    const val Jib = "3.1.4"
    const val Kover = "0.4.2"
    const val SourceSets = "4.0.0"
    const val TaskTree = "2.1.0"
    const val TestLogger = "3.1.0"
}

object Versions {
    // kotlin
    const val Kotlin = PluginVersions.Kotlin
    const val KotlinXCoroutines = "1.5.2"
    const val KotlinXSerialization = "1.3.1"

    const val FigureRetry = "1.0.12"

    // 3rd Party
    const val Arrow = "1.0.1"
    const val Config4k = "0.4.2"
    const val Exposed = "0.36.2"
    const val Flyway = PluginVersions.Flyway
    const val HikariCP = "5.0.0"
    const val Kafka = "2.4.1"
    const val Kotest = "4.6.3"
    const val KotestExtensionsArrow = "1.1.1"
    const val KotestExtensionsKtor = "1.0.3"
    const val KotestExtensionsTestContainers = "1.0.1"
    const val KtorFeatureFlyway = "1.3.0"
    const val KotlinLogging = "2.0.11"
    const val Ktor = "1.6.5"
    const val Lettuce = "6.1.5.RELEASE"
    const val Logback = "1.2.7"
    const val Mockk = "1.12.1"
    const val ProjectReactor = "3.4.12"
    const val ProjectReactorKafka = "1.3.7"
    const val ProjectReactorKotlinExtensions = "1.1.5"
    const val Postgres = "42.3.1"
    const val SLF4J = "1.7.30"
    const val TestContainers = "1.16.2"
}

object Libraries {
    // Kotlin
    const val KotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"
    const val KotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8"
    const val KotlinScriptRuntime = "org.jetbrains.kotlin:kotlin-script-runtime"
    const val KotlinXCoRoutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesDebug = "org.jetbrains.kotlinx:kotlinx-coroutines-debug:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesJDK8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesReactive = "org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesReactor = "org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.KotlinXCoroutines}"
    const val KotlinXCoRoutinesSLF4J = "org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:${Versions.KotlinXCoroutines}"
    const val KotlinXSerializationJSON = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KotlinXSerialization}"

    const val FigureRetry = "com.figure.retry:figure-retry:${Versions.FigureRetry}"

    // 3rd Party
    const val ArrowStack = "io.arrow-kt:arrow-stack:${Versions.Arrow}"
    const val ArrowCore = "io.arrow-kt:arrow-core"
    const val ArrowFx = "io.arrow-kt:arrow-fx-coroutines"
    const val ArrowMeta = "io.arrow-kt:arrow-meta:${Versions.Arrow}" // kapt doesn't look at the BOM apparently?
    const val ArrowOptics = "io.arrow-kt:arrow-optics"

    const val Config4k = "io.github.config4k:config4k:${Versions.Config4k}" // also consider hoplite
    const val ExposedCore = "org.jetbrains.exposed:exposed-core:${Versions.Exposed}"
    const val ExposedJDBC = "org.jetbrains.exposed:exposed-jdbc:${Versions.Exposed}"
    const val ExposedJavaTime = "org.jetbrains.exposed:exposed-java-time:${Versions.Exposed}"
    const val FlywayCore = "org.flywaydb:flyway-core:${Versions.Flyway}"
    const val HikariCP = "com.zaxxer:HikariCP:${Versions.HikariCP}"
    const val JMXMP = "org.glassfish.external:opendmk_jmxremote_optional_jar:1.0-b01-ea"
    const val KafkaClients = "org.apache.kafka:kafka-clients:${Versions.Kafka}"

    const val Kotest = "io.kotest:kotest-runner-junit5-jvm:${Versions.Kotest}"
    const val KotestAssertions = "io.kotest:kotest-assertions-core-jvm:${Versions.Kotest}"
    const val KotestAssertionsArrow = "io.kotest.extensions:kotest-assertions-arrow:${Versions.KotestExtensionsArrow}"
    const val KotestAssertionsKtor = "io.kotest.extensions:kotest-assertions-ktor:${Versions.KotestExtensionsKtor}"
    const val KotestTestContainers = "io.kotest.extensions:kotest-extensions-testcontainers:${Versions.KotestExtensionsTestContainers}"

    const val KotlinLogging = "io.github.microutils:kotlin-logging-jvm:${Versions.KotlinLogging}"
    const val KtorFeatureFlyway = "com.viartemev:ktor-flyway-feature:${Versions.KtorFeatureFlyway}"
    const val KtorHTML = "io.ktor:ktor-html-builder:${Versions.Ktor}"
    const val KtorAuth = "io.ktor:ktor-auth:${Versions.Ktor}"
    const val KtorAuthJWT = "io.ktor:ktor-auth-jwt:${Versions.Ktor}"
    const val KtorClientCore = "io.ktor:ktor-client-core:${Versions.Ktor}"
    const val KtorClientCIO = "io.ktor:ktor-client-cio:${Versions.Ktor}"
    const val KtorClientLogging = "io.ktor:ktor-client-logging:${Versions.Ktor}"
    const val KtorClientSerialization = "io.ktor:ktor-client-serialization:${Versions.Ktor}"
    const val KtorServerSession = "io.ktor:ktor-server-sessions:${Versions.Ktor}"
    const val KtorServerNetty = "io.ktor:ktor-server-netty:${Versions.Ktor}"
    const val KtorSerialization = "io.ktor:ktor-serialization:${Versions.Ktor}"
    const val KtorServerTests = "io.ktor:ktor-server-tests:${Versions.Ktor}"
    const val KtorWebsockets = "io.ktor:ktor-websockets:${Versions.Ktor}"
    const val Lettuce = "io.lettuce:lettuce-core:${Versions.Lettuce}"
    const val LogbackClassic = "ch.qos.logback:logback-classic:${Versions.Logback}"
    const val Mockk = "io.mockk:mockk:${Versions.Mockk}"
    const val Postgres = "org.postgresql:postgresql:${Versions.Postgres}"

    const val ProjectReactorCore = "io.projectreactor:reactor-core:${Versions.ProjectReactor}"
    const val ProjectReactorKafka = "io.projectreactor.kafka:reactor-kafka:${Versions.ProjectReactorKafka}"
    const val ProjectReactorKotlinExtensions = "io.projectreactor.kotlin:reactor-kotlin-extensions:${Versions.ProjectReactorKotlinExtensions}"

    const val SLF4JAPI = "org.slf4j:slf4j-api:${Versions.SLF4J}"

    const val TestContainers = "org.testcontainers:testcontainers:${Versions.TestContainers}"
    const val TestContainersJUnit = "org.testcontainers:junit-jupiter:${Versions.TestContainers}"
    const val TestContainersKafka = "org.testcontainers:kafka:${Versions.TestContainers}"
    const val TestContainersPostgres = "org.testcontainers:postgresql:${Versions.TestContainers}"
}

// gradle configurations
const val api = "api"
const val implementation = "implementation"
const val runtimeOnly = "runtimeOnly"
const val testCompileOnly = "testCompileOnly"
const val testImplementation = "testImplementation"
const val testRuntimeOnly = "testRuntimeOnly"
const val developmentOnly = "developmentOnly"
const val integrationTestImplementation = "integrationTestImplementation"
const val integrationTestRuntimeOnly = "integrationTestRuntimeOnly"
