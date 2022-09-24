package org.fred

import com.typesafe.config.ConfigFactory
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import org.fred.plugins.*
import org.slf4j.LoggerFactory

suspend fun CoroutineScope.daemonCoroutine(block: suspend () -> Unit) {
    val logger = LoggerFactory.getLogger("daemonCoroutine")
    while (isActive) {
        try {
            block()
            yield()
        } catch (e: Exception) {
            logger.error("Error: ${e.message} in daemon coroutine. Continue serve", e)
        }
    }
}


fun main() {
    val config = ConfigFactory.load() ?: throw IllegalStateException("Can't load config")
    val statService = StatService()
    val requestService = RequestService(config, statService)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureAdministration()
        configureSerialization()
        configureMonitoring()
        configureApiRouting(requestService)
        configureStatRouting(statService)
    }.start(wait = true)
}
