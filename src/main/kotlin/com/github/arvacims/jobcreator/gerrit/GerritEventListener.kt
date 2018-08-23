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
        log.info("Gerrit event: ${event.eventType}")
    }

    fun gerritEvent(event: RefUpdated) {
        log.info(event.refUpdate.toLogMessage())
        val project = event.refUpdate.project
        val branch = event.refUpdate.getBranch()
        if ((event.refUpdate.isProjectCreation() || event.refUpdate.isBranchCreation()) && !event.refUpdate.isChangesBranch())
            jenkinsService.createOrUpdateJob(project, branch)
    }
}

fun RefUpdate.getBranch(): String {
    val branch = this.refName.substringAfterLast("/")
    return if (branch == "config")                                  // "refs/meta/config" is updated on project creation
        "master"
    else
        branch
}

fun RefUpdate.isProjectCreation(): Boolean {
    val branch = this.refName.substringAfterLast("/")
    return branch == "config" && this.isInitial()
}

fun RefUpdate.isBranchCreation(): Boolean {
    val branch = this.refName.substringAfterLast("/")
    return branch != "master" && this.isInitial()
}

fun RefUpdate.isInitial(): Boolean {
    val oldRev = this.oldRev
    return oldRev.all { it == "0".single() }
}

fun RefUpdate.isChangesBranch(): Boolean {
    return this.refName.substringAfter("/").substringBefore("/") == "changes"
}

fun RefUpdate.toLogMessage(): String = "Gerrit refUpdated event - " +
        "Project: '${this.project}', " +
        "oldRev: '${this.oldRev}', " +
        "newRev: '${this.newRev}', " +
        "ref: '${this.ref}', " +
        "refName: '${this.refName}'"
