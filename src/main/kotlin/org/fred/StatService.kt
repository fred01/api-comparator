package org.fred

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

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

}