package io.github.nefilim.ktorpatterns.config

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract
import mu.KotlinLogging
import java.time.Duration

private val logger = KotlinLogging.logger { }

data class DatabaseConfig(
    val databaseName: String,
    val driver: String,
    val url: String,
    val username: String,
    val password: String,
    val schema: String,
    val hikari: HikariCPConfig,
)

data class HikariCPConfig(
    val autoCommit: Boolean = false,
    val connectionTimeout: Long = Duration.ofSeconds(30).toMillis(),
//    val idleTimeout: Long = Duration.ofSeconds(30).toMillis(),
    val keepaliveTime: Long = Duration.ofSeconds(0).toMillis(),
    val maxLifetime: Long = Duration.ofMinutes(30).toMillis(),
    val maximumPoolSize: Int = 10,
    val leakDetectionThreshold: Long = Duration.ofSeconds(10).toMillis(),
    val validationTimeout: Long = Duration.ofSeconds(5).toMillis(),
)

data class KtorPatternsApplicationConfig(
    val databaseConfig: DatabaseConfig,
)

fun buildConfiguration(configFilename: String = configurationFilename(System.getenv())): KtorPatternsApplicationConfig {
    // TODO is there a way we can get the underlying Typesafe Config from Ktor?
    logger.info {"Loading configuration from $configFilename" }
    val config = ConfigFactory.load(configFilename)
    val databaseConfig = config.extract<DatabaseConfig>("database")

    return KtorPatternsApplicationConfig(
        databaseConfig,
    )
}

fun configurationFilename(env: Map<String, String>): String {
    val profile = env.get("SPRING_PROFILES_ACTIVE") ?: "container" // can probably start phasing out this env variable on the deployment side and then here too
    return "application-$profile.conf"
}