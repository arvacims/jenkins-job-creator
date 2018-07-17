package com.github.arvacims.jobcreator

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.io.InputStream

class RestTemplateLoggingInterceptor : ClientHttpRequestInterceptor {

    private val log = LoggerFactory.getLogger(RestTemplateLoggingInterceptor::class.java)

    override fun intercept(
            request: HttpRequest,
            body: ByteArray,
            exec: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val timeIn = System.nanoTime()
        val response = exec.execute(request, body)
        logRequest(request, response, timeIn)
        return response
    }

    private fun logRequest(request: HttpRequest, response: ClientHttpResponse, timeIn: Long) {
        val requestMethod = request.method
        val requestPath = request.uri
        val responseStatus = response.rawStatusCode
        val durationInMs = (System.nanoTime() - timeIn) / 1000000
        val headers = response.headers
        val content = response.body.toLog()
        log.info("Request $requestMethod $requestPath got response $responseStatus $headers $content ${durationInMs}ms")
    }

}

private fun InputStream.toLog(): String = String(this.readBytes(), Charsets.UTF_8)
        .replace("\n", "").replace("\r", "")
