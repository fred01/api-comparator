package org.fred.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import org.fred.RequestInfo
import org.fred.RequestService
import java.time.Instant
import java.util.*


fun Application.configureApiRouting(requestService: RequestService) {
    install(DoubleReceive)

    routing {
        route("/{...}") {
            handle {
                val requestInfo = RequestInfo(
                    UUID.randomUUID().toString(),
                    Instant.now().toEpochMilli(),
                    call.request.httpMethod.value,
                    call.receiveText(),
                    call.request.uri,
                    call.request.headers.toMap()
                )
                application.log.info("Request ${requestInfo.requestId}")
                requestService.asyncRequestChannel.send(requestInfo)

                val response = requestService.executeReferenceRequest(requestInfo)
                call.response.status(HttpStatusCode.Created)
                // call.response.headers.append("Content-type", "application/json")
                call.respondText("""
                    {"a":"b"}
                """.trimIndent())
            }
        }
//        get("/") {
//            call.respondText("Hello World!")
//        }
//        post("/double-receive") {
//            val first = call.receiveText()
//            val theSame = call.receiveText()
//            call.respondText(first + " " + theSame)
//        }
    }
}
