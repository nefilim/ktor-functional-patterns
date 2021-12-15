# Ktor Patterns

Small sample application to illustrate various common Ktor/Kotlin/Coroutine patterns:

* Logging with KotlinLogging
* General Ktor application layout
* Configuration with immutable data classes & HOCON configuration files
* Using KotlinX JSON serialization
* Health monitor for database
* Schema maintenance with Flyway Ktor plugin
* Persistence with Exposed
  * Using the IO Dispatcher for blocking operations
  * Retrying transient exceptions
* Functional services without DI 
* Unit Testing
* Integration testing with TestContainers (Postgres)

A Docker container can be built using [Jib](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin):

`./gradlew :server:jibDockerBuild --image=nefilim/ktor-functional-patterns`
                      
## Running Locally
    
Use `docker-compose up` to start Postgres/Kafka etc

Add Ktor Run configuration with the following:
* **Ktor main class:** `io.github.nefilim.ktorpatterns.MainKt`
* **Program arguments:** `-config=server/src/main/resources/application-development.conf`
* **Environment variables:** `SPRING_PROFILES_ACTIVE=development`
                                                   
Click `Run`

## TODO

* Test and document SemVer support
* Moar! Unit testing
