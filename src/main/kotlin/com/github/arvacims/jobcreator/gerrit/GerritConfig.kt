package com.github.arvacims.jobcreator.gerrit

import com.sonymobile.tools.gerrit.gerritevents.GerritConnectionConfig2
import com.sonymobile.tools.gerrit.gerritevents.ssh.Authentication
import com.sonymobile.tools.gerrit.gerritevents.watchdog.WatchTimeExceptionData
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import java.io.File

class GerritConfig(
        private val user: String,
        private val hostname: String,
        private val sshPort: Int,
        private val sshKeyFile: String,
        private val sshKeyPass: String?
) : GerritConnectionConfig2 {

    override fun getGerritHostName(): String {
        return hostname
    }

    override fun getGerritUserName(): String {
        return user
    }

    override fun getGerritAuthKeyFilePassword(): String {
        return sshKeyPass ?: ""
    }

    override fun getGerritEMail(): String {
        return ""
    }

    override fun getGerritSshPort(): Int {
        return sshPort
    }

    override fun getWatchdogTimeoutSeconds(): Int {
        return 0
    }

    override fun getHttpCredentials(): Credentials {
        return UsernamePasswordCredentials("", "")
    }

    override fun getGerritAuthKeyFile(): File {
        return File(sshKeyFile)
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
        return Authentication(File(sshKeyFile), user, sshKeyPass)
    }

    override fun getWatchdogTimeoutMinutes(): Int {
        return 0
    }

}
