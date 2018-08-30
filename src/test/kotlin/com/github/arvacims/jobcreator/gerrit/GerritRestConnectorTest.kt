package com.github.arvacims.jobcreator.gerrit

import com.github.arvacims.jobcreator.EnvironmentFromResource
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore("Needs remote instances and custom configuration.")
class GerritRestConnectorTest {

    private lateinit var gerritRestConnector: GerritRestConnector

    @Before
    fun setUp() {
        gerritRestConnector = GerritRestConnector(EnvironmentFromResource())
    }

    @Test
    fun `should get all branches of all active projects`() {
        // when
        val projectBranches = gerritRestConnector.getProjectBranches()

        // then
        Assert.assertNotNull(projectBranches)
    }

}
