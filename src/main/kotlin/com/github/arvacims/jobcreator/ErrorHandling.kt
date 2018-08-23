package com.github.arvacims.jobcreator

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun onException(request: HttpServletRequest, exception: Exception): ResponseEntity<WsError> {
        log.warn("Request {} failed due to an unexpected exception.", request.requestURI, exception)
        return exception.toWsErrorResponse(type = GENERIC_ERROR_TYPE, message = GENERIC_ERROR_MESSAGE)
    }

}

data class WsError(val type: String, val message: String)

private const val GENERIC_ERROR_TYPE = "InternalServerError"
private const val GENERIC_ERROR_MESSAGE = "An error occurred."

fun Exception.toWsErrorResponse(
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        type: String? = null,
        message: String? = null
): ResponseEntity<WsError> {
    val errorType = type ?: javaClass.simpleName
    val errorMessage = message ?: this.message ?: GENERIC_ERROR_MESSAGE
    return ResponseEntity.status(httpStatus).body(WsError(errorType, errorMessage))
}
