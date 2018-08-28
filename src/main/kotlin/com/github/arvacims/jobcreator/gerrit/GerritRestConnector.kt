package com.github.arvacims.jobcreator.gerrit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.arvacims.jobcreator.RestTemplateLoggingInterceptor
import org.springframework.core.env.Environment
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.http.client.support.BasicAuthorizationInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GerritRestConnector(env: Environment) {

    private val restTemplate = restTemplate(env)
    private val gerritUrl = env.getRequiredProperty("gerrit.rest.baseUrl")

    fun getProjectBranches(): List<ProjectBranch> {
        return getProjects()
                .flatMap { project ->
                    getBranches(project.value.id)
                            .map { ProjectBranch(project.value, it) }
                }
    }

    private fun getProjects(): Map<String, GerritProject> {
        val url = "$gerritUrl/a/projects/?t"

        val response = restTemplate.getForObject(url, String::class.java)

        return jacksonObjectMapper().readValue(response!!.substringAfter(")]}'"))
    }

    private fun getBranches(projectId: String): List<GerritBranch> {
        val url = "$gerritUrl/a/projects/$projectId/branches"

        val response = restTemplate.getForObject(url, String::class.java)

        val branches = jacksonObjectMapper().readValue<List<GerritBranch>>(response!!.substringAfter(")]}'"))

        return branches.filter { it.ref.startsWith("refs/heads/") }
    }

}

data class ProjectBranch(val project: GerritProject, val branch: GerritBranch)

private fun restTemplate(env: Environment): RestTemplate {
    val requestFactory = HttpComponentsClientHttpRequestFactory().apply {
        setConnectTimeout(env.getRequiredProperty("rest.timeout.connect", Int::class.java))
        setReadTimeout(env.getRequiredProperty("rest.timeout.read", Int::class.java))
    }

    val restTemplate = RestTemplate(BufferingClientHttpRequestFactory(requestFactory))

    val authorizationInterceptor = BasicAuthorizationInterceptor(
            env.getRequiredProperty("gerrit.user"),
            env.getRequiredProperty("gerrit.rest.password")
    )

    restTemplate.interceptors.add(authorizationInterceptor)
    restTemplate.interceptors.add(RestTemplateLoggingInterceptor())

    return restTemplate
}
