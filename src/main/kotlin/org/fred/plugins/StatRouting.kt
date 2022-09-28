package org.fred.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.fred.StatService


fun Application.configureStatRouting(statService: StatService) {

    routing {
        get("/stat") {
            call.respondText("Hello World!") // TODO: General stat, like total requests count, failed and so on
        }
        get("/stat/requests") {
            call.respond(statService.getRequestsList())
        }
        get("/stat/request/{requestId}") {
            val requestId = call.parameters["requestId"]
            if (requestId == null) {
                call.respondText("Missed required parameter")
                return@get
            }
            val requestStat = statService.getRequestStat(requestId)
            if (requestStat == null) {
                call.respondText("Request with ID: $requestId not found")
                return@get
            }

            call.respond(requestStat)
        }
    }
}
