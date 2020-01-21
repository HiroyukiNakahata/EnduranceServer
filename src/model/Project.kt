package com.endurance.model

data class Project(
  val project_id: Int,
  val project_name: String,
  val client: String
) {
  constructor(): this(0, "", "")
}

interface IProjectService {
  fun findProject(): List<Project>
  fun findProject(id: Int): Project
  fun insertProject(project: Project)
  fun updateProject(project: Project)
  fun deleteProject(id: Int)
}
