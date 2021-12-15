package io.github.nefilim.ktorpatterns.persistence

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import mu.KLogger
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Duration

data class DomainOperations(
    val userOperations: UserDBOperations,
)

class InsertOrUpdate<Key: Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : InsertStatement<Key>(table, isIgnore) {

    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = table.columns.joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}

fun <T : Table> T.insertOrUpdate(vararg keys: Column<*>, body: T.(InsertStatement<Number>) -> Unit): InsertOrUpdate<Number> {
    return InsertOrUpdate<Number>(this, keys = keys).apply {
        body(this)
        execute(TransactionManager.current())
    }
}

// Retry library is not open source... yet

//fun eitherTransientRetryDecider(@Suppress("UNUSED_PARAMETER") retryStatus: RetryStatus, t: Throwable): RetryAction {
//    val isTransientException = anyCause(t) { cause ->
//        when (cause) {
//            is java.sql.SQLTransientConnectionException -> true
//            else -> false
//        }
//    }
//    return when (isTransientException) {
//        true -> RetryAction.ConsultRetryPolicy
//        false -> RetryAction.DontRetry
//    }
//}
//
//internal val defaultRetryPolicy = (equalJitter(Duration.ofMillis(1000)) * limitRetries(5)).maximumDelay(Duration.ofSeconds(20))

internal suspend fun <T>runWithContextAndTransaction(logger: KLogger, block: Transaction.() -> T): Either<Throwable, T> {
//    return recover(
//            defaultRetryPolicy,
//            ::eitherTransientRetryDecider,
//            onRetry = logRetry(logger, defaultRetryPolicy),
//        ) {
           return Either.catch {
               newSuspendedTransaction(Dispatchers.IO) {
                   if (logger.isDebugEnabled)
                       addLogger(StdOutSqlLogger)
                   block()
               }
           }
//        }
}