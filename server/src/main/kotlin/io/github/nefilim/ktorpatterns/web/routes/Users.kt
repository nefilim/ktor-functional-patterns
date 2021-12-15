package io.github.nefilim.ktorpatterns.web.routes

import arrow.core.Either
import arrow.core.Option
import io.github.nefilim.ktorpatterns.domain.ErrorResponse
import io.github.nefilim.ktorpatterns.domain.RegisterUserRequest
import io.github.nefilim.ktorpatterns.domain.RegisteredUser
import io.github.nefilim.ktorpatterns.domain.UserID
import io.github.nefilim.ktorpatterns.service.anyCause
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import mu.KotlinLogging
import org.postgresql.util.PSQLException

private val logger = KotlinLogging.logger { }

fun Route.users(
    registerUser: suspend (RegisterUserRequest) -> Either<Throwable, RegisteredUser>,
    getUser: suspend (UserID) -> Either<Throwable, Option<RegisteredUser>>,
) {
    post("/user") {
        val registerUserRequest = call.receive<RegisterUserRequest>()
        registerUser(registerUserRequest).fold({
            logger.error(it) { "failed to register user: $registerUserRequest" }
            when {
                isDuplicateKeyException(it) -> call.respond(HttpStatusCode.Conflict, ErrorResponse("user already exists"))
                else -> call.respond(HttpStatusCode.InternalServerError, ErrorResponse("failed to register user"))
            }
        }, {
            call.respond(HttpStatusCode.Created, it)
        })
    }

    get("/user/{id}") {
        call.parameters["id"]?.let {
            getUser(UserID.fromString(it)).fold({
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("failed to find user"))
            }, {
                it.fold({
                    call.respond(HttpStatusCode.NotFound, "{}")
                }, {
                    call.respond(it)
                })
            })
        } ?: run {
            logger.warn { "no id supplied to find user" }
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("missing user ID"))
        }
    }
}

private fun isDuplicateKeyException(t: Throwable): Boolean {
    return anyCause(t) {
        when (it) {
            is PSQLException -> it.serverErrorMessage?.message?.contains("duplicate key value violates unique constraint") == true
            else -> false
        }
    }
}