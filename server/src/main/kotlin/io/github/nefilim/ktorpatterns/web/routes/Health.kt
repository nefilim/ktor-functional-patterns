package io.github.nefilim.ktorpatterns.web.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.flow.StateFlow

fun Route.healthRouting(
    dataSourceMonitor: StateFlow<Boolean>
) {
    route("/healthz") {
        get {
            when (dataSourceMonitor.value) {
                true -> call.respondText("Ok", status = HttpStatusCode.OK)
                false -> call.respondText("Fail", status = HttpStatusCode.InternalServerError)
            }
        }
    }
    route("/readyz") {
        get {
            when (dataSourceMonitor.value) {
                true -> call.respondText("Ok", status = HttpStatusCode.OK)
                false -> call.respondText("Fail", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}