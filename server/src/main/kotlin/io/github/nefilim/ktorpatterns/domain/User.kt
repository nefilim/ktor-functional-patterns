package io.github.nefilim.ktorpatterns.domain

import io.github.nefilim.ktorpatterns.web.LocalDateSerializer
import io.github.nefilim.ktorpatterns.web.LocalDateTimeSerializer
import io.github.nefilim.ktorpatterns.web.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Serializable
data class RegisterUserRequest(
    val username: String,
    val email: String,
    @Serializable(LocalDateSerializer::class) @SerialName("birth_date") val birthDate: LocalDate,
)

@Serializable
data class RegisteredUser(
    val id: UserID,
    val email: String,
    val username: String,
    @Serializable(LocalDateSerializer::class) @SerialName("birth_date") val birthDate: LocalDate,
    @Serializable(LocalDateTimeSerializer::class) val createdTimestamp: LocalDateTime,
    @Serializable(LocalDateTimeSerializer::class) val lastUpdateTimestamp: LocalDateTime,
)

@JvmInline
@Serializable
value class UserID(@Serializable(UUIDSerializer::class) val id: UUID) {
    companion object {
        fun fromString(id: String): UserID = UserID(UUID.fromString(id))
    }
}

fun uuidGenerator(): UUID = UUID.randomUUID()

typealias UserIDGenerator = () -> UserID
val defaultUserIDGenerator: UserIDGenerator = { uuidGenerator().toUserID() }

fun UUID.toUserID(): UserID = UserID(this)