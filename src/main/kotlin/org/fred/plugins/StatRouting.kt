package org.fred.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.fred.StatService


fun Application.configureStatRouting(statService: StatService) {

    routing {
        get("/stat") {
            call.respondText("Hello World!")
        }
        get("/requests") {
            call.respondText("Hello World!")
        }
        get("/request/{requestId}") {
            call.respondText("Hello World!")
        }
    }
}
