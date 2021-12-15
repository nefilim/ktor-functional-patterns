package io.github.nefilim.ktorpatterns

import io.github.nefilim.ktorpatterns.config.buildConfiguration
import io.github.nefilim.ktorpatterns.domain.TimeProvider
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime

object TestConstructs {
    val config = buildConfiguration("application-integration-test.conf")
    val format = Json

    val staticLocalDateTime: LocalDateTime = LocalDateTime.parse("2007-12-03T10:15:30")
    val staticTimeProvider: TimeProvider = { io.github.nefilim.ktorpatterns.TestConstructs.staticLocalDateTime }
}

object TestContainers {
    object Postgres {
        val logger = KotlinLogging.logger("postgres")
    }
}
