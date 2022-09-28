package org.fred

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class RequestShortStat(
    val requestId: String,
    val requestTimestamp: Long,
    val referenceResponseCode: Int?,
    val sampleResponseCode: Int?,
)

@Serializable
data class RequestStat(
    val requestId: String,
    val requestInfo: RequestInfo,
    val referenceInfo: ResponseInfo?,
    val sampleResponseInfo: ResponseInfo?,
    val responsesTest: String
)

class StatService {
    private val requestList = mutableListOf<String>() // IDs of arrived requests
    private val requests = mutableMapOf<String, RequestInfo>()
    private val referenceResponses = mutableMapOf<String, ResponseInfo>()
    private val sampleResponses = mutableMapOf<String, ResponseInfo>()
    val storeRequestChannel = Channel<RequestInfo>(10000)
    val storeReferenceChannel = Channel<ResponseInfo>(10000)
    val storeSampleChannel = Channel<ResponseInfo>(10000)

    init {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            daemonCoroutine {
                for (request in storeRequestChannel) {
                    requestList.add(request.requestId)
                    requests[request.requestId] = request
                }
            }
        }
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            daemonCoroutine {
                for (response in storeReferenceChannel) {
                    referenceResponses[response.requestId] = response
                }
            }
        }
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            daemonCoroutine {
                for (response in storeSampleChannel) {
                    sampleResponses[response.requestId] = response
                }
            }
        }
    }

    fun getRequestsList(): List<RequestShortStat> =
        requestList.mapNotNull { requestId ->
            requests[requestId]
        }.map { requestInfo ->
            val rri = referenceResponses[requestInfo.requestId]
            val sri = sampleResponses[requestInfo.requestId]
            RequestShortStat(
                requestInfo.requestId,
                requestInfo.requestTime,
                rri?.status,
                sri?.status
            )
        }


    fun getRequestStat(requestId: String): RequestStat? {
        val ri = requests[requestId] ?: return null
        val rri = referenceResponses[requestId]
        val sri = sampleResponses[requestId]
        return RequestStat(
            requestId,
            ri, rri, sri,
            if (rri != null && sri != null) {
                // TODO: Calculate full response difference
                if (rri.status == sri.status) {
                    "Equal"
                } else {
                    "Not equal"
                }
            } else {
                "N/A"
            }

        )
    }
}