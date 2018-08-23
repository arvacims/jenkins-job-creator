package com.github.arvacims.jobcreator.jenkins

import com.github.arvacims.jobcreator.EnvironmentFromResource
import com.github.arvacims.jobcreator.createGerritConfig
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
        val jenkinsConnector = JenkinsConnector(env)
        jenkinsService = JenkinsService(gerritConfig, jenkinsConnector, env)
    }

    @Test
    fun `should create or update and afterwards get job`() {
        // given
        val project = "demo-project"
        val branch = "demo-branch"

        // when
        jenkinsService.createOrUpdateJob(project, branch)
        val config = jenkinsService.getJobConfig("${project}_$branch")

        // then
        Assert.assertNotNull(config)
    }

    @Test
    fun `should get job names`() {
        // when
        val jobNames = jenkinsService.getJobNames()

        // then
        Assert.assertTrue(jobNames.isNotEmpty())
    }

}
