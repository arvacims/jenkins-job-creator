package com.github.arvacims.jobcreator

import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class JenkinsConnector(env: Environment) {

    private val restTemplate = restTemplate(env)
    private val jenkinsUrl = env.getRequiredProperty("jenkins.baseUrl")

    fun createJob(jobConfigXml: String, jobName: String): String? {

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$jenkinsUrl/createItem")
                .queryParam("name", jobName)

        val crumbHeader = getCrumbHeader()
        val request = HttpEntity(jobConfigXml, crumbHeader)
        val response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, request, String::class.java)

        return response.body
    }

    fun getJobConfig(jobName: String): String {

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$jenkinsUrl/job/$jobName/config.xml")

        val crumbHeader = getCrumbHeader()
        val request = HttpEntity(null, crumbHeader)
        val response = try {
            restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, request, String::class.java)
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.NOT_FOUND)
                throw JenkinsJobNotFoundException(jobName)
            else
                throw JenkinsErrorException(jobName, e)
        }

        return response.body!!
    }

    fun getJobs(): Set<String> {

        val uri = UriComponentsBuilder.fromHttpUrl("$jenkinsUrl/api/json")
                .queryParam("tree", "jobs[name]")
                .build()
                .encode()
                .toUri()

        val crumbHeader = getCrumbHeader()
        val request = HttpEntity(null, crumbHeader)
        val response = restTemplate.exchange(uri, HttpMethod.GET, request, JenkinsJobs::class.java)

        return response.body!!.jobs.map { it.name }.toSet()
    }

    fun updateJob(jobConfigXml: String, jobName: String): String? {

        val uriBuilder = UriComponentsBuilder.fromHttpUrl("$jenkinsUrl/job/$jobName/config.xml")

        val crumbHeader = getCrumbHeader()
        val request = HttpEntity(jobConfigXml, crumbHeader)
        val response = try {
            restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, request, String::class.java)
        } catch (e: HttpClientErrorException) {
            if (e.statusCode == HttpStatus.NOT_FOUND)
                throw JenkinsJobNotFoundException(jobName)
            else
                throw JenkinsErrorException(jobName, e)
        }

        return response.body
    }

    private fun getCrumbHeader(): HttpHeaders {
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

class JenkinsJobNotFoundException(jobName: String) : Exception("Requested jenkins job '$jobName' does not exist.") {
    val status = HttpStatus.NOT_FOUND
}

class JenkinsErrorException(jobName: String, exception: HttpClientErrorException) :
        Exception("Error while requesting jenkins job '$jobName'", exception) {

    val status: HttpStatus = exception.statusCode
}

private fun restTemplate(env: Environment): RestTemplate {
    val requestFactory = HttpComponentsClientHttpRequestFactory().apply {
        setConnectTimeout(env.getRequiredProperty("rest.timeout.connect", Int::class.java))
        setReadTimeout(env.getRequiredProperty("rest.timeout.read", Int::class.java))
    }
    val restTemplate = RestTemplate(BufferingClientHttpRequestFactory(requestFactory))
    restTemplate.interceptors.add(
            BasicAuthorizationInterceptor(
                    env.getRequiredProperty("jenkins.user"),
                    env.getRequiredProperty("jenkins.password")
            )
    )
    restTemplate.interceptors.add(RestTemplateLoggingInterceptor())
    return restTemplate
}
