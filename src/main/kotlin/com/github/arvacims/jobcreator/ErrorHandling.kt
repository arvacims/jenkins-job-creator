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
    fun onException(request: HttpServletRequest, exception: Exception): ResponseEntity<Error> {
        log.warn("Request {} failed.", request.requestURI, exception)
        return exception.toWsErrorResponse(type = "InternalServerError", message = GENERIC_ERROR_MESSAGE)
    }

}

data class Error(val type: String, val message: String)

private const val GENERIC_ERROR_MESSAGE = "An error occurred."

fun Exception.toWsErrorResponse(
        httpStatus: HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
        type: String? = null,
        message: String? = null
): ResponseEntity<Error> {
    val errorType = type ?: javaClass.simpleName
    val errorMessage = message ?: this.message ?: GENERIC_ERROR_MESSAGE
    return ResponseEntity.status(httpStatus).body(Error(errorType, errorMessage))
}
