package com.github.arvacims.jobcreator

import com.sonymobile.tools.gerrit.gerritevents.GerritConnectionConfig2
import com.sonymobile.tools.gerrit.gerritevents.ssh.Authentication
import com.sonymobile.tools.gerrit.gerritevents.watchdog.WatchTimeExceptionData
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import java.io.File

class GerritConfig(
        private val gerritHostName: String,
        private val gerritUser: String,
        private val gerritSshPort: Int,
        private val gerritSshKeyFile: String
) : GerritConnectionConfig2 {

    override fun getGerritHostName(): String {
        return gerritHostName
    }

    override fun getGerritUserName(): String {
        return gerritUser
    }

    override fun getGerritAuthKeyFilePassword(): String {
        return ""
    }

    override fun getGerritEMail(): String {
        return ""
    }

    override fun getGerritSshPort(): Int {
        return gerritSshPort
    }

    override fun getWatchdogTimeoutSeconds(): Int {
        return 0
    }

    override fun getHttpCredentials(): Credentials {
        return UsernamePasswordCredentials("", "")
    }

    override fun getGerritAuthKeyFile(): File {
        return File(gerritSshKeyFile)
    }

    override fun getGerritProxy(): String {
        return ""
    }

    override fun getGerritFrontEndUrl(): String {
        return ""
    }

    override fun getExceptionData(): WatchTimeExceptionData {
        return WatchTimeExceptionData()
    }

    override fun getGerritAuthentication(): Authentication {
        return Authentication(File(gerritSshKeyFile), gerritUser)
    }

    override fun getWatchdogTimeoutMinutes(): Int {
        return 0
    }

}
