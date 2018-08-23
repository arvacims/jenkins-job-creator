package com.github.arvacims.jobcreator

import com.github.arvacims.jobcreator.jenkins.JenkinsErrorException
import com.github.arvacims.jobcreator.jenkins.JenkinsJobNotFoundException
import com.github.arvacims.jobcreator.jenkins.JenkinsService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/jobs")
class JobCreatorController(private val jenkinsService: JenkinsService) {

    private val log = LoggerFactory.getLogger(JobCreatorController::class.java)

    @PostMapping("/create/all")
    fun createOrUpdateAllJobs() {
        // TODO: Use Gerrit REST API to find all projects & branches and then create or update all jobs.
        throw NotImplementedError()
    }

    @PostMapping("/create/{project}/{branch}")
    fun createOrUpdateJob() {
        // TODO: Create or update the job.
        throw NotImplementedError()
    }

    @GetMapping("/{jobName}")
    fun getJobConfig(@PathVariable jobName: String): String =
            jenkinsService.getJobConfig(jobName)

    @PostMapping("/{jobName}")
    fun updateJobConfig(@PathVariable jobName: String, @RequestBody configXml: String) {
        jenkinsService.updateJobConfig(jobName, configXml)
    }

    @ExceptionHandler(JenkinsJobNotFoundException::class)
    fun onJenkinsJobNotFoundException(
            request: HttpServletRequest,
            exception: JenkinsJobNotFoundException
    ): ResponseEntity<WsError> {
        log.warn("Request {} failed as the Jenkins job could not be found.", request.requestURI)
        return exception.toWsErrorResponse(httpStatus = HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(JenkinsErrorException::class)
    fun onJenkinsErrorException(
            request: HttpServletRequest,
            exception: JenkinsErrorException
    ): ResponseEntity<WsError> {
        log.warn("Request {} failed due to some error when requesting the Jenkins API.", request.requestURI, exception)
        return exception.toWsErrorResponse()
    }

}
