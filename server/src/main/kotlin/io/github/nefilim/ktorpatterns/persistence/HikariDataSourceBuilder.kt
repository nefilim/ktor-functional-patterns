package io.github.nefilim.ktorpatterns.persistence

import io.github.nefilim.ktorpatterns.config.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

class ShutdownHookHikariDataSource(private val shutdownHooks: List<Runnable>, config: HikariConfig) : HikariDataSource(config) {
    override fun close() {
        shutdownHooks.forEach(Runnable::run)
        super.close()
    }

    companion object {
        fun build(
            databaseConfig: DatabaseConfig,
            properties: Map<String, String> = emptyMap(),
            shutdownHooks: List<Runnable> = emptyList(),
        ): ShutdownHookHikariDataSource {
            val config = HikariConfig()

            config.jdbcUrl = databaseConfig.url
            config.username = databaseConfig.username
            config.password = databaseConfig.password
//            config.schema = databaseConfig.schema DO NOT configure schema, results in transactions being opened automatically, doesn't play nice with exposed
            properties.forEach { config.addDataSourceProperty(it.key, it.value) }

            with (databaseConfig.hikari) {
                config.isAutoCommit = autoCommit
                config.connectionTimeout = connectionTimeout
//                config.idleTimeout = idleTimeout
                config.keepaliveTime = keepaliveTime
                config.maxLifetime = maxLifetime
                config.maximumPoolSize = maximumPoolSize
                config.leakDetectionThreshold = leakDetectionThreshold
                config.validationTimeout = validationTimeout
            }

            return ShutdownHookHikariDataSource(shutdownHooks, config)
        }
    }
}