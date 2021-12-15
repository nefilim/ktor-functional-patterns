package io.github.nefilim.ktorpatterns

import arrow.core.Either
import arrow.core.Option
import arrow.core.toOption
import io.github.nefilim.ktorpatterns.config.buildConfiguration
import io.github.nefilim.ktorpatterns.domain.RegisterUserRequest
import io.github.nefilim.ktorpatterns.domain.RegisteredUser
import io.github.nefilim.ktorpatterns.domain.TimeProvider
import io.github.nefilim.ktorpatterns.domain.UserID
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

object TestConstructs {
    val config = buildConfiguration("application-test.conf")
    val format = Json { }

    val staticLocalDateTime: LocalDateTime = LocalDateTime.parse("2007-12-03T10:15:30")
    val staticTimeProvider: TimeProvider = { staticLocalDateTime }
}

object MockUserDBOperations {

    suspend fun createUser(userID: UserID, registerUserRequest: RegisterUserRequest, timeProvider: TimeProvider): Either<Throwable, RegisteredUser> {
        val now = timeProvider()
        return Either.Right(
            RegisteredUser(
                userID,
                registerUserRequest.email,
                registerUserRequest.username,
                registerUserRequest.birthDate,
                now,
                now,
            )
        )
    }

    fun getUser(registeredUser: RegisteredUser): suspend (UserID) -> Either<Throwable, Option<RegisteredUser>> = { _ ->
        Either.Right(
            registeredUser.toOption()
        )
    }
}