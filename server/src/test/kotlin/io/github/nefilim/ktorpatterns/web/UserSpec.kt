package io.github.nefilim.ktorpatterns.web

import io.github.nefilim.ktorpatterns.MockUserDBOperations
import io.github.nefilim.ktorpatterns.TestConstructs.config
import io.github.nefilim.ktorpatterns.TestConstructs.format
import io.github.nefilim.ktorpatterns.TestConstructs.staticTimeProvider
import io.github.nefilim.ktorpatterns.domain.RegisterUserRequest
import io.github.nefilim.ktorpatterns.domain.RegisteredUser
import io.github.nefilim.ktorpatterns.domain.UserIDGenerator
import io.github.nefilim.ktorpatterns.domain.defaultUserIDGenerator
import io.github.nefilim.ktorpatterns.mainRoutes
import io.github.nefilim.ktorpatterns.persistence.UserDBOperations
import io.kotest.assertions.fail
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import java.time.LocalDate
import java.util.*

class UserSpec: WordSpec() {
    init {
        "Users Route" should {
            "GET endpoint return a User" {
                val registeredUser = RegisteredUser(defaultUserIDGenerator(), "tester@github.com", "tester", LocalDate.now(), staticTimeProvider(), staticTimeProvider())
                val userDBOperations = UserDBOperations(get = MockUserDBOperations.getUser(registeredUser))

                withTestApplication({ mainRoutes(config, userDBOperations) }) {
                    val id = UUID.randomUUID()
                    handleRequest(HttpMethod.Get, "/user/$id").apply {
                        response shouldHaveStatus HttpStatusCode.OK
                        response.content?.also {
                            format.decodeFromString(RegisteredUser.serializer(), it) shouldBe registeredUser
                        } ?: fail("did not receive a registered user for ${registeredUser.id}")
                    }
                }
            }

            "POST endpoint should create and return user" {
                val userDBOperations = UserDBOperations(create = MockUserDBOperations::createUser)
                val userID = defaultUserIDGenerator()
                val staticUserIDGenerator: UserIDGenerator = { userID }

                withTestApplication({ mainRoutes(config, userDBOperations, staticUserIDGenerator, staticTimeProvider) }) {
                    val request = RegisterUserRequest("theuser", "email@google.com", LocalDate.now())
                    val registeredUser = RegisteredUser(staticUserIDGenerator(), request.email, request.username, request.birthDate, staticTimeProvider(), staticTimeProvider())

                    with(handleRequest(HttpMethod.Post, "/user") {
                        addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                        setBody(format.encodeToString(RegisterUserRequest.serializer(), request))
                    }) {
                        response shouldHaveStatus HttpStatusCode.Created
                        response.content?.also {
                            format.decodeFromString(RegisteredUser.serializer(), it) shouldBe registeredUser
                        } ?: fail("did not receive a registered user for ${registeredUser.id}")
                    }
                }
            }
        }
    }
}