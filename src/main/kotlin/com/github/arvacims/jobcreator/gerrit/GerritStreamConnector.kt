package com.github.arvacims.jobcreator.gerrit

import com.sonymobile.tools.gerrit.gerritevents.GerritConnection
import com.sonymobile.tools.gerrit.gerritevents.GerritHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class GerritStreamConnector(
        private val gerritConfig: GerritConfig,
        private val gerritEventListener: GerritEventListener
) {

    private val log = LoggerFactory.getLogger(GerritStreamConnector::class.java)

    @PostConstruct
    private fun init() {
        val hostName = gerritConfig.gerritHostName

        log.info("Connecting to '{}' ...", hostName)

        GerritConnection(hostName, gerritConfig).apply {
            handler = GerritHandler().apply { addListener(gerritEventListener) }
            start()
        }

        log.info("Connected to '{}'.", hostName)
    }

}
