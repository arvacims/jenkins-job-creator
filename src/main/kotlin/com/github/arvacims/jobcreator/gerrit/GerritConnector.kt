package com.github.arvacims.jobcreator.gerrit

import com.sonymobile.tools.gerrit.gerritevents.GerritConnection
import com.sonymobile.tools.gerrit.gerritevents.GerritHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class GerritServerConnector(
        private val gerritConfig: GerritConfig,
        private val gerritEventListener: GerritEventListener
) {

    private val log = LoggerFactory.getLogger(GerritServerConnector::class.java)

    @PostConstruct
    private fun init() {
        log.info("Establishing connection to Gerrit server: ${gerritConfig.gerritHostName}")

        val connection = GerritConnection(gerritConfig.gerritHostName, gerritConfig)
        val handler = GerritHandler()
        handler.addListener(gerritEventListener)
        connection.handler = handler
        connection.start()

        log.info("Connection to ${gerritConfig.gerritHostName} established")
    }

}
