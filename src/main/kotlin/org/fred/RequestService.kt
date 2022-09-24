package org.fred

import com.typesafe.config.Config
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.config.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import java.time.Instant

@Serializable
data class RequestInfo(
    val requestId: String,
    val requestTime: Long,
    val method: String,
    val body: String,
    val url: String,
    val headers: Map<String, List<String>>
)

@Serializable
data class ResponseInfo(
    val responseTime: Long,
    val requestId: String,
    val status: Int,
    val body: String
)


class RequestService(config: Config, private val statService: StatService) {
    private val logger = LoggerFactory.getLogger("RequestService")
    val asyncRequestChannel = Channel<RequestInfo>(10000)
    private val referenceBaseUrl =
        config.tryGetString("api.reference.baseUrl") ?: throw IllegalStateException("Reference URL not set")
    private val sampleBaseUrl =
        config.tryGetString("api.sample.baseUrl") ?: throw IllegalStateException("Sample URL not set")
    private val referenceClientTimeout = config.getLong("api.reference.timeout")
    private val sampleClientTimeout = config.getLong("api.sample.timeout")
    private val referenceHttpClient = HttpClient {
        this.expectSuccess = false
        install(HttpTimeout) {
            this.requestTimeoutMillis = referenceClientTimeout
        }
    }
    private val sampleHttpClient = HttpClient {
        this.expectSuccess = false
        install(HttpTimeout) {
            this.requestTimeoutMillis = sampleClientTimeout
        }
    }

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            daemonCoroutine {
                for (requestInfo in asyncRequestChannel) {
                    val responseInfo = sampleHttpClient.request(configureHttpRequest(requestInfo, sampleBaseUrl)).toResponseInfo(requestInfo.requestId)
                    statService.storeSampleChannel.send(responseInfo)
                }
            }
        }
    }

    suspend fun executeReferenceRequest(requestInfo: RequestInfo): ResponseInfo {
        val responseInfo = referenceHttpClient.request(configureHttpRequest(requestInfo, referenceBaseUrl)).toResponseInfo(requestInfo.requestId)
        statService.storeReferenceChannel.send(responseInfo)
        return responseInfo
    }

    private fun configureHttpRequest(requestInfo: RequestInfo, baseUrl: String): HttpRequestBuilder {
        return HttpRequestBuilder().apply {
            this.method = HttpMethod(requestInfo.method)
            this.url(baseUrl.removeSuffix("/") + requestInfo.url)
            this.setBody(requestInfo.body)
            this.headers {
                requestInfo.headers.forEach {
                    this.appendAll(it.key, it.value)
                }
            }
        }
    }

    private suspend fun HttpResponse.toResponseInfo(requestId: String): ResponseInfo = ResponseInfo(
        responseTime = Instant.now().toEpochMilli(),
        requestId = requestId,
        status = status.value,
        body = bodyAsText()
    )
}