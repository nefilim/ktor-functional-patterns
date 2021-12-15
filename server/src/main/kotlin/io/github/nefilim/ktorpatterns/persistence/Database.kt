package io.github.nefilim.ktorpatterns.persistence

import arrow.core.Either
import arrow.core.getOrElse
import io.github.nefilim.ktorpatterns.config.DatabaseConfig
import io.github.nefilim.ktorpatterns.service.monitorFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import java.time.Duration
import javax.sql.DataSource

private val logger = KotlinLogging.logger { }

val DefaultSchema = "ktorpatterns"

fun connectDatabase(databaseConfig: DatabaseConfig): DataSource {
    return ShutdownHookHikariDataSource.build(databaseConfig).also {
        Database.connect(it)
            .also { TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_READ_COMMITTED }
            .also { logger.info("Database connected") }
    }
}

private const val DefaultTimeoutSeconds = 5

// TODO support validation query?
suspend fun isDataSourceHealthy(dataSource: DataSource): Boolean {
    return Either.catch { dataSource.connection.use { it.isValid(DefaultTimeoutSeconds) } }.getOrElse { false }
}

@OptIn(DelicateCoroutinesApi::class)
fun monitorDataSource(
    dataSource: DataSource,
    checkDataSource: suspend (DataSource) -> Boolean = ::isDataSourceHealthy,
    interval: Duration = Duration.ofSeconds(5),
    jobCoroutineScope: CoroutineScope = GlobalScope,
): StateFlow<Boolean> {
    
    val mutableStateFlow = MutableStateFlow(false)
    val stateFlow = mutableStateFlow.asStateFlow() // read-only state flow

    fun monitoringFlow(): Flow<Boolean> {
        return flow {
            while (true) {
                delay(interval)
                emit(checkDataSource(dataSource))
            }
        }
        .onEach {
            mutableStateFlow.emit(it)
        }
    }

    jobCoroutineScope.launch(Dispatchers.IO) {
        monitorFlow("db-healthcheck", ::monitoringFlow, Dispatchers.IO, logger)
    }

    return stateFlow
}