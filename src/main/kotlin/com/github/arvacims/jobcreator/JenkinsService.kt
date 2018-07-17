package com.github.arvacims.jobcreator

import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class JenkinsService(env: Environment, private val gerritConfig: GerritConfig, private val jenkinsConnector: JenkinsConnector) {

    private val log = LoggerFactory.getLogger(JenkinsService::class.java)

    private val defaultConfig = javaClass.getResource("/jenkins-jobs/config.xml").readText()

    private val jenkinsGerritUser = env.getRequiredProperty("jenkins.gerritUser")

    fun createJob(project: String, branch: String) {
        log.info("Create Jenkins job for project '$project' branch '$branch'")

        val projectName = if (branch == "master") project else "$project-$branch"

        jenkinsConnector.createJob(createProjectConfig(defaultConfig, project, branch), projectName)
    }

    fun getDefaultJobConfig(): String {
        return defaultConfig
    }

    fun getJobConfig(jobName: String): String {
        return jenkinsConnector.getJobConfig(jobName)
    }

    fun updateJobConfig(config: String, jobName: String): String? {
        return jenkinsConnector.updateJob(config, jobName)
    }

    fun updateAllJobConfigs(_config: String?): Set<String> {
        val jobs = jenkinsConnector.getJobs()
        val config = _config ?: getDefaultJobConfig()
        jobs.forEach { updateJobConfig(config, it) }
        return jobs
    }

    private fun createProjectConfig(config: String, project: String, branch: String): String {
        return config
                .replace("{PROJECT_NAME}", project)
                .replace("{BRANCH}", branch)
                .replace("{GERRIT_USER}", jenkinsGerritUser)
                .replace("{GERRIT_HOST_NAME}", gerritConfig.gerritHostName)
                .replace("{GERRIT_PORT}", gerritConfig.gerritSshPort.toString())
    }

}
