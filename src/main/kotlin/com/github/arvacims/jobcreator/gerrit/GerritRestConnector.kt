package com.github.arvacims.jobcreator.gerrit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.arvacims.jobcreator.restTemplate
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GerritRestConnector(env: Environment) {

    private val gerritUrl = env.getRequiredProperty("gerrit.rest.baseUrl")
    private val gerritUser = env.getRequiredProperty("gerrit.user")
    private val gerritPassword = env.getRequiredProperty("gerrit.rest.password")

    private val restTemplate: RestTemplate

    init {
        restTemplate = restTemplate(env, gerritUser, gerritPassword)
    }

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
