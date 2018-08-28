package com.github.arvacims.jobcreator.jenkins

import com.github.arvacims.jobcreator.EnvironmentFromResource
import com.github.arvacims.jobcreator.createGerritConfig
import com.github.arvacims.jobcreator.gerrit.GerritRestConnector
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("Needs remote instances and custom configuration.")
class JenkinsServiceTest {

    private lateinit var jenkinsService: JenkinsService

    @Before
    fun setUp() {
        val env = EnvironmentFromResource()
        val gerritConfig = createGerritConfig(env)
        val gerritRestConnector = GerritRestConnector(env)
        val jenkinsConnector = JenkinsConnector(env)
        jenkinsService = JenkinsService(gerritConfig, gerritRestConnector, jenkinsConnector, env)
    }

    @Test
    fun `should create or update and afterwards get job`() {
        // given
        val project = "demo-project"
        val branch = "demo-branch"

        // when
        jenkinsService.createOrUpdateJobs(project, branch)
        val configMerge = jenkinsService.getJobConfig("${project}_$branch")
        val configReview = jenkinsService.getJobConfig("${project}_${branch}_review")

        // then
        Assert.assertNotNull(configMerge)
        Assert.assertNotNull(configReview)
    }

    @Test
    fun `should get job names`() {
        // when
        val jobNames = jenkinsService.getJobNames()

        // then
        Assert.assertTrue(jobNames.isNotEmpty())
    }

}
