package com.github.arvacims.jobcreator.jenkins

import com.github.arvacims.jobcreator.gerrit.GerritConfig
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File

@Service
class JenkinsService(
        private val gerritConfig: GerritConfig,
        private val jenkinsConnector: JenkinsConnector,
        env: Environment
) {

    private val log = LoggerFactory.getLogger(JenkinsService::class.java)

    private val configTemplate = File("data/config.xml").readText()
    private val gerritUser = env.getRequiredProperty("jenkins.gerritUser")

    fun createOrUpdateJob(project: String, branch: String) {
        log.info("Creating or updating Jenkins job for project '{}' / branch '{}' ...", project, branch)

        val jobName = "${project}_$branch"
        val configXml = fillConfigTemplate(project, branch)

        try {
            val currentConfigXml = jenkinsConnector.getJobConfig(jobName)
            log.info("Jenkins job '{}' was found.", jobName)

            if (currentConfigXml.forDiff() == configXml.forDiff()) {
                log.info("Jenkins job '{}' is still up to date.", jobName)
            } else {
                log.info("Jenkins job '{}' is outdated. Updating it ...", jobName)
                jenkinsConnector.updateJobConfig(jobName, configXml)
            }
        } catch (e: JenkinsJobNotFoundException) {
            log.info("Jenkins job '{}' was not found. Creating it ...", jobName)
            jenkinsConnector.createJob(jobName, configXml)
        }
    }

    fun getJobNames(): Set<String> =
            jenkinsConnector.getJobNames()

    fun getJobConfig(jobName: String): String =
            jenkinsConnector.getJobConfig(jobName)

    fun updateJobConfig(jobName: String, configXml: String) {
        jenkinsConnector.updateJobConfig(jobName, configXml)
    }

    private fun fillConfigTemplate(project: String, branch: String): String =
            configTemplate
                    .replace("{PROJECT_NAME}", project)
                    .replace("{BRANCH}", branch)
                    .replace("{GERRIT_USER}", gerritUser)
                    .replace("{GERRIT_HOST_NAME}", gerritConfig.gerritHostName)
                    .replace("{GERRIT_PORT}", gerritConfig.gerritSshPort.toString())

}

private fun String.forDiff(): String =
        lines().joinToString(separator = "", transform = { it.trim() })
