package io.github.nefilim.ktorpatterns.service

import arrow.core.Either
import arrow.core.Option
import io.github.nefilim.ktorpatterns.domain.RegisterUserRequest
import io.github.nefilim.ktorpatterns.domain.RegisteredUser
import io.github.nefilim.ktorpatterns.domain.TimeProvider
import io.github.nefilim.ktorpatterns.domain.UserID
import io.github.nefilim.ktorpatterns.domain.UserIDGenerator
import io.github.nefilim.ktorpatterns.domain.defaultTimeProvider
import io.github.nefilim.ktorpatterns.domain.defaultUserIDGenerator
import io.github.nefilim.ktorpatterns.persistence.UserDBOperations
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

fun createUser(
    userDBOperations: UserDBOperations,
    userIDGenerator: UserIDGenerator = defaultUserIDGenerator,
    timeProvider: TimeProvider = defaultTimeProvider,
): suspend (RegisterUserRequest) -> Either<Throwable, RegisteredUser> = { request ->
    userIDGenerator().let { userID ->
        logger.info { "creating user $userID with $request" }
        userDBOperations.create(userID, request, timeProvider)
    }
}

fun getUser(
    userDBOperations: UserDBOperations,
): suspend (UserID) -> Either<Throwable, Option<RegisteredUser>> = { userID ->
    logger.info { "getting user $userID" }
    userDBOperations.get(userID)
}