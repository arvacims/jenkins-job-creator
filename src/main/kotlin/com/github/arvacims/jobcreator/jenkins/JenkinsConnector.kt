package com.github.arvacims.jobcreator.jenkins

import com.github.arvacims.jobcreator.restTemplate
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class JenkinsConnector(env: Environment) {

    private val jenkinsUrl = env.getRequiredProperty("jenkins.baseUrl")
    private val jenkinsUser = env.getRequiredProperty("jenkins.user")
    private val jenkinsPassword = env.getRequiredProperty("jenkins.password")

    private val restTemplate: RestTemplate

    init {
        restTemplate = restTemplate(env, jenkinsUser, jenkinsPassword)
    }

    fun createJob(jobName: String, configXml: String) {
        val uri = UriComponentsBuilder.fromHttpUrl("$jenkinsUrl/createItem")
                .queryParam("name", jobName)
                .build()
                .encode()
                .toUri()

        val request = HttpEntity(configXml, createRequestHeaders())
        restTemplate.exchange(uri, HttpMethod.POST, request, String::class.java)
    }

    fun getJobNames(): Set<String> {
        val uri = UriComponentsBuilder.fromHttpUrl("$jenkinsUrl/api/json")
                .queryParam("tree", "jobs[name]")
                .build()
                .encode()
                .toUri()

        val request = HttpEntity(null, createRequestHeaders())
        val response = restTemplate.exchange(uri, HttpMethod.GET, request, JenkinsJobs::class.java)

        return response.body!!.jobs.map { it.name }.toSet()
    }

    fun getJobConfig(jobName: String): String =
            requestConfigXml(jobName, null).body!!

    fun updateJobConfig(jobName: String, configXml: String) {
        requestConfigXml(jobName, configXml)
    }

    private fun requestConfigXml(jobName: String, configXml: String?): ResponseEntity<String> {
        val url = "$jenkinsUrl/job/$jobName/config.xml"
        val method = if (configXml == null) HttpMethod.GET else HttpMethod.POST

        val request = HttpEntity(configXml, createRequestHeaders())

        return try {
            restTemplate.exchange(url, method, request, String::class.java)
        } catch (clientError: HttpClientErrorException) {
            when (clientError.statusCode) {
                HttpStatus.NOT_FOUND -> throw JenkinsJobNotFoundException(jobName)
                else                 -> throw JenkinsErrorException(jobName, clientError)
            }
        }
    }

    private fun createRequestHeaders(): HttpHeaders {
        val url = "$jenkinsUrl/crumbIssuer/api/json"
        val response = restTemplate.getForEntity(url, Crumb::class.java)
        val crumb = response.body!!

        val headers = HttpHeaders()
        headers.set("Content-Type", "text/xml")
        headers.set(crumb.crumbRequestField, crumb.crumb)

        return headers
    }

}

data class Crumb(val _class: String, val crumb: String, val crumbRequestField: String)

data class JenkinsJobs(val _class: String, val jobs: Set<Job>)

data class Job(val _class: String, val name: String)

class JenkinsJobNotFoundException(jobName: String) :
        Exception("Requested Jenkins job '$jobName' was not found.")

class JenkinsErrorException(jobName: String, exception: HttpClientErrorException) :
        Exception("Requesting Jenkins job '$jobName' failed.", exception)
