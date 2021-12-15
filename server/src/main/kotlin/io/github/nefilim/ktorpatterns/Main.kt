package io.github.nefilim.ktorpatterns

import io.github.nefilim.ktorpatterns.web.routes.healthRouting
import io.github.nefilim.ktorpatterns.config.KtorPatternsApplicationConfig
import io.github.nefilim.ktorpatterns.config.buildConfiguration
import io.github.nefilim.ktorpatterns.domain.TimeProvider
import io.github.nefilim.ktorpatterns.domain.UserIDGenerator
import io.github.nefilim.ktorpatterns.domain.defaultTimeProvider
import io.github.nefilim.ktorpatterns.domain.defaultUserIDGenerator
import io.github.nefilim.ktorpatterns.persistence.DefaultSchema
import io.github.nefilim.ktorpatterns.persistence.UserDBOperations
import io.github.nefilim.ktorpatterns.persistence.connectDatabase
import io.github.nefilim.ktorpatterns.persistence.monitorDataSource
import io.github.nefilim.ktorpatterns.service.createUser
import io.github.nefilim.ktorpatterns.service.getUser
import io.github.nefilim.ktorpatterns.web.routes.users
import com.viartemev.ktor.flyway.FlywayFeature
import com.viartemev.ktor.flyway.Info
import com.viartemev.ktor.flyway.Migrate
import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.util.*
import kotlinx.coroutines.flow.StateFlow
import mu.KotlinLogging
import org.slf4j.event.Level
import java.lang.management.ManagementFactory
import java.util.*
import javax.management.MBeanServer
import javax.management.remote.JMXConnectorServerFactory
import javax.management.remote.JMXServiceURL

private val logger = KotlinLogging.logger { }

// fail fast
val DefaultTimeZone = TimeZone.getTimeZone("UTC")!!
val DefaultTimeZoneID = DefaultTimeZone.toZoneId()!!

fun main(args: Array<String>) {
    TimeZone.setDefault(DefaultTimeZone)
    io.ktor.server.netty.EngineMain.main(args)
}

@OptIn(KtorExperimentalAPI::class)
fun Application.module() {
    val config = buildConfiguration()
    val dataSource = connectDatabase(config.databaseConfig)
    val dataSourceMonitor = monitorDataSource(dataSource)

    val httpClient = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                prettyPrint = false
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }
    install(FlywayFeature) {
        this.dataSource = dataSource //required
        commands(Info, Migrate) //optional, default command list is: Info, Migrate
        schemas = arrayOf(DefaultSchema) // optional, default value is the DB product specific default schema
    }

    healthRoutes(dataSourceMonitor)
    mainRoutes(config, UserDBOperations())

    registerEvents()
}

fun Application.healthRoutes(
    dataSourceMonitor: StateFlow<Boolean>,
) {
    routing { // equiv to install(Routing)
        healthRouting(dataSourceMonitor)
    }
}

fun Application.mainRoutes(
    config: KtorPatternsApplicationConfig,
    userDBOperations: UserDBOperations,
    userIDGenerator: UserIDGenerator = defaultUserIDGenerator,
    timeProvider: TimeProvider = defaultTimeProvider,
) {
    install(StatusPages) {
        exception<Throwable> {
            logger.error(it) { "unhandled exception" }
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
    install(ContentNegotiation) {
        json(kotlinx.serialization.json.Json {
            prettyPrint = false
            isLenient = true
        })
    }
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            "path: ${call.request.path()} auth header: ${call.request.headers["Authorization"]} cookies: ${call.request.cookies.rawCookies}"
        }
    }

    routing { // equiv to install(Routing)
        trace { logger.debug { it.buildText() } }

        users(
            createUser(userDBOperations, userIDGenerator, timeProvider),
            getUser(userDBOperations),
        )

        static("/dashboard/assets") {
            resources("css")
        }
    }
}

fun Application.registerEvents() {
    environment.monitor.subscribe(ApplicationStarting) {
        log.info("application is starting! $it")
    }
    environment.monitor.subscribe(ApplicationStopping) {
        log.info("application is SHUTTING DOWN!")
    }
    environment.monitor.subscribe(ApplicationStarted) {
        log.info("application started! $it")
    }
}

// needed when wanting to connect with JMX console to the process
private fun startJMXServer() {
    // Get the MBean server for monitoring/controlling the JVM
    val mBeanServer: MBeanServer = ManagementFactory.getPlatformMBeanServer()

    // Create a JMXMP connector server
    val url = JMXServiceURL("jmxmp", "localhost", 1098)
    val cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null, mBeanServer)
    cs.start()
}