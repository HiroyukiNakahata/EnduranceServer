package com.endurance.model

data class Project(
  val project_id: Int,
  val project_name: String,
  val client: String
)

interface IProjectService {
  fun findProject(): List<Project>
  fun findProject(id: Int): Project
  fun insertProject(project: Project)
  fun updateProject(project: Project)
  fun deleteProject(id: Int)
}
