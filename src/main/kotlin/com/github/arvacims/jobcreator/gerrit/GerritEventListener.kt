package com.github.arvacims.jobcreator.gerrit

import com.github.arvacims.jobcreator.jenkins.JenkinsService
import com.sonymobile.tools.gerrit.gerritevents.GerritEventListener
import com.sonymobile.tools.gerrit.gerritevents.dto.GerritEvent
import com.sonymobile.tools.gerrit.gerritevents.dto.attr.RefUpdate
import com.sonymobile.tools.gerrit.gerritevents.dto.events.RefUpdated
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GerritEventListener(private val jenkinsService: JenkinsService) : GerritEventListener {

    private val log = LoggerFactory.getLogger(GerritEventListener::class.java)

    override fun gerritEvent(event: GerritEvent) {
        if (event is RefUpdated) onRefUpdated(event)
    }

    private fun onRefUpdated(event: RefUpdated) {
        val refUpdate = event.refUpdate
        log.info(refUpdate.toLogMessage())

        val isHeadUpdate = refUpdate.ref.startsWith("refs/heads/")
        val isInitial = refUpdate.oldRev == "0000000000000000000000000000000000000000"

        // Only trigger Jenkins when a NEW BRANCH is created!
        if (isHeadUpdate && isInitial) {
            val project = refUpdate.project
            val branch = refUpdate.refName
            jenkinsService.createOrUpdateJob(project, branch)
        }
    }
}

private fun RefUpdate.toLogMessage(): String =
        "RefUpdate(project=$project, oldRev=$oldRev, newRev=$newRev, ref=$ref, refName=$refName)"
