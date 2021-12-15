package io.github.nefilim.ktorpatterns.persistence

import arrow.core.Either
import arrow.core.Option
import arrow.core.toOption
import io.github.nefilim.ktorpatterns.domain.RegisterUserRequest
import io.github.nefilim.ktorpatterns.domain.RegisteredUser
import io.github.nefilim.ktorpatterns.domain.TimeProvider
import io.github.nefilim.ktorpatterns.domain.UserID
import io.github.nefilim.ktorpatterns.domain.toUserID
import io.github.nefilim.ktorpatterns.persistence.UserTable.createUser
import io.github.nefilim.ktorpatterns.persistence.UserTable.getUser
import mu.KotlinLogging
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

data class UserDBOperations(
    val create: suspend (UserID, RegisterUserRequest, TimeProvider) -> Either<Throwable, RegisteredUser> = ::createUser,
    val get: suspend (UserID) -> Either<Throwable, Option<RegisteredUser>> = ::getUser
)

object UserTable : Table(name = "$DefaultSchema.users") {
    private val logger = KotlinLogging.logger { }

    val userID = uuid("id")
    val username = varchar("username", 100)
    val email = varchar("email", 100)
    val birthDate = date("birth_date")
    val createdTimestamp = datetime("created_timestamp")
    val lastUpdatedTimestamp = datetime("last_updated_timestamp")

    override val primaryKey: PrimaryKey = PrimaryKey(userID, name = "PK_User_ID")

    suspend fun createUser(
        id: UserID,
        user: RegisterUserRequest,
        timeProvider: TimeProvider,
    ): Either<Throwable, RegisteredUser> {
        return runWithContextAndTransaction(logger) {
            val now = timeProvider()
            UserTable.insert {
                it[this.userID] = id.id
                it[this.username] = user.username
                it[email] = user.email
                it[this.birthDate] = user.birthDate
                it[this.createdTimestamp] = now
                it[this.lastUpdatedTimestamp] = now
            }.resultedValues?.let {
                fromResultRow(it.first())
            }!! // TODO
        }
    }

    suspend fun getUser(
        id: UserID,
    ): Either<Throwable, Option<RegisteredUser>> {
        return runWithContextAndTransaction(logger) {
            UserTable.select { userID eq id.id }.singleOrNull()?.let {
                fromResultRow(it)
            }.toOption()
        }
    }

    private fun fromResultRow(row: ResultRow): RegisteredUser {
        return RegisteredUser(
            row[userID].toUserID(),
            row[email],
            row[username],
            row[birthDate],
            row[createdTimestamp],
            row[lastUpdatedTimestamp],
        )
    }
}