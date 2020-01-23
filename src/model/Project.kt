package com.endurance.model

data class Project(
  val project_id: Int,
  val project_name: String,
  val client: String
) {
  constructor(): this(0, "", "")
}

interface IProjectService {
  fun find(): List<Project>
  fun find(id: Int): Project
  fun insert(project: Project)
  fun update(project: Project)
  fun delete(id: Int)
}
