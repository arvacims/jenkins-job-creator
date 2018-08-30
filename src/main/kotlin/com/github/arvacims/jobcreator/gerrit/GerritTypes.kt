package com.github.arvacims.jobcreator.gerrit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectInfo(val id: String, val state: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class BranchInfo(val ref: String)

data class GerritProject(val name: String, val id: String, val state: String)

data class GerritBranch(val name: String)

data class ProjectBranch(val project: GerritProject, val branch: GerritBranch)
