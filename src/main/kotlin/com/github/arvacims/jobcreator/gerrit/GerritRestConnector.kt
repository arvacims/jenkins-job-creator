package com.github.arvacims.jobcreator.gerrit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.arvacims.jobcreator.restTemplateGerrit
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class GerritRestConnector(env: Environment) {

    private val gerritUrl = env.getRequiredProperty("gerrit.rest.baseUrl")
    private val restTemplate = restTemplateGerrit(env)

    fun getProjectBranches(): List<ProjectBranch> =
            getProjects().flatMap { project ->
                getBranches(project).map { ProjectBranch(project, it) }
            }

    private fun getProjects(): List<GerritProject> {
        val url = "$gerritUrl/a/projects/?type=CODE"
        val response = restTemplate.getForObject(url, String::class.java)
        val jsonStr = response!!.substringAfter(")]}'")

        val projectsById = jacksonObjectMapper().readValue<Map<String, ProjectInfo>>(jsonStr)
        return projectsById.map { GerritProject(it.key, it.value.id, it.value.state) }
                .filter { it.state == "ACTIVE" }
                .filterNot { it.id == "All-Users" }
    }

    private fun getBranches(project: GerritProject): List<GerritBranch> {
        val url = "$gerritUrl/a/projects/${project.id}/branches"
        val response = restTemplate.getForObject(url, String::class.java)
        val jsonStr = response!!.substringAfter(")]}'")

        val branches = jacksonObjectMapper().readValue<List<BranchInfo>>(jsonStr)
        return branches.filter { it.ref.startsWith("refs/heads/") }.map { GerritBranch(it.ref.removeRange(0..10)) }
    }

}
