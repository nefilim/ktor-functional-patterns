package io.github.nefilim.ktorpatterns.web

import io.github.nefilim.ktorpatterns.TestConstructs.config
import io.github.nefilim.ktorpatterns.TestConstructs.format
import io.github.nefilim.ktorpatterns.TestConstructs.staticTimeProvider
import io.github.nefilim.ktorpatterns.TestContainers
import io.github.nefilim.ktorpatterns.domain.RegisterUserRequest
import io.github.nefilim.ktorpatterns.domain.RegisteredUser
import io.github.nefilim.ktorpatterns.domain.UserIDGenerator
import io.github.nefilim.ktorpatterns.domain.defaultUserIDGenerator
import io.github.nefilim.ktorpatterns.mainRoutes
import io.github.nefilim.ktorpatterns.persistence.DefaultSchema
import io.github.nefilim.ktorpatterns.persistence.ShutdownHookHikariDataSource
import io.github.nefilim.ktorpatterns.persistence.UserDBOperations
import io.kotest.assertions.fail
import io.kotest.assertions.ktor.shouldHaveStatus
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.testcontainers.perSpec
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import io.ktor.server.testing.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.testcontainers.containers.Network
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.utility.DockerImageName
import java.time.LocalDate

class UserSpec: WordSpec() {
    private val network = Network.newNetwork()

    private val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:13"))
        .withDatabaseName(config.databaseConfig.databaseName)
        .withUsername(config.databaseConfig.username)
        .withPassword(config.databaseConfig.password)
        .withExposedPorts(5432)
        .withNetwork(network)

    init {
        listener(postgresContainer.perSpec())

        "Users Route" should {
            val userID = defaultUserIDGenerator()
            val staticUserIDGenerator: UserIDGenerator = { userID }
            val userDBOperations = UserDBOperations()
            val request = RegisterUserRequest("theuser", "email@google.com", LocalDate.now())
            val registeredUser = RegisteredUser(staticUserIDGenerator(), request.email, request.username, request.birthDate, staticTimeProvider(), staticTimeProvider())

            "POST endpoint should create and return user" {
                withTestApplication({ mainRoutes(config, userDBOperations, staticUserIDGenerator, staticTimeProvider) }) {
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

            "GET endpoint return a User" {
                withTestApplication({ mainRoutes(config, userDBOperations) }) {
                    handleRequest(HttpMethod.Get, "/user/${userID.id}").apply {
                        response shouldHaveStatus HttpStatusCode.OK
                        response.content?.also {
                            format.decodeFromString(RegisteredUser.serializer(), it) shouldBe registeredUser
                        } ?: fail("did not receive a registered user for ${registeredUser.id}")
                    }
                }
            }

        }
    }

    override fun beforeSpec(spec: Spec) {
        if (io.github.nefilim.ktorpatterns.TestContainers.Postgres.logger.isDebugEnabled)
            postgresContainer.followOutput(Slf4jLogConsumer(io.github.nefilim.ktorpatterns.TestContainers.Postgres.logger))
        val dataSource = ShutdownHookHikariDataSource.build(config.databaseConfig.copy(url = "jdbc:postgresql://${postgresContainer.containerIpAddress}:${postgresContainer.firstMappedPort}/${config.databaseConfig.databaseName}"))
        Flyway.configure().schemas(DefaultSchema).dataSource(dataSource).load().migrate()
        TransactionManager.defaultDatabase = Database.connect(dataSource)
    }
}