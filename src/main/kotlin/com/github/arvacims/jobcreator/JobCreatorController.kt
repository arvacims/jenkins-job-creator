package com.github.arvacims.jobcreator

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpMediaTypeNotAcceptableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/jenkins/job-configs")
class JobCreatorController(private val jenkinsService: JenkinsService) {

    private val log = LoggerFactory.getLogger(JobCreatorController::class.java)

    @RequestMapping(
            method = [RequestMethod.GET],
            path = ["/default"]
    )
    fun getDefaultJenkinsJobConfig(): String {
        return jenkinsService.getDefaultJobConfig()
    }

    @RequestMapping(
            method = [RequestMethod.POST],
            path = ["/update/default"]
    )
    fun updateAllJenkinsJobs(): Set<String> {
        return jenkinsService.updateAllJobConfigs(null)
    }

    @RequestMapping(
            method = [RequestMethod.POST],
            path = ["/update"]
    )
    fun updateAllJenkinsJobs(@RequestBody config: String): Set<String> {
        return jenkinsService.updateAllJobConfigs(config)
    }

    @RequestMapping(
            method = [RequestMethod.GET],
            path = ["/{jobName}"]
    )
    fun getJenkinsJob(@PathVariable jobName: String): String {
        return jenkinsService.getJobConfig(jobName)
    }

    @RequestMapping(
            method = [RequestMethod.POST],
            path = ["/{jobName}/update"]
    )
    fun updateJenkinsJob(@PathVariable jobName: String, @RequestBody config: String): String? {
        return jenkinsService.updateJobConfig(config, jobName)
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotAcceptableException::class)
    fun handleHttpMediaTypeNotAcceptableException(): String {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE
    }

    @ExceptionHandler(JenkinsJobNotFoundException::class)
    fun onJenkinsJobNotFoundException(
            request: HttpServletRequest,
            exception: JenkinsJobNotFoundException
    ): ResponseEntity<Error> {
        log.warn("Request {} failed due to non existing jenkins job.", request.requestURI, exception)
        return exception.toWsErrorResponse(httpStatus = exception.status)
    }

    @ExceptionHandler(JenkinsErrorException::class)
    fun onJenkinsErrorException(
            request: HttpServletRequest,
            exception: JenkinsErrorException
    ): ResponseEntity<Error> {
        log.warn("Request {} failed due to error when requesting jenkins api.", request.requestURI, exception)
        return exception.toWsErrorResponse(httpStatus = exception.status)
    }

}
