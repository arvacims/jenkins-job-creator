package com.github.arvacims.jobcreator.gerrit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GerritProject(
        val id: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GerritBranch(
        val ref: String
)
