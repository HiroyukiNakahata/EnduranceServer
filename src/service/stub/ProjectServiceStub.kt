package com.endurance.service.stub

import com.endurance.model.IProjectService
import com.endurance.model.Project

class ProjectServiceStub : IProjectService {
  override fun find(): List<Project> {
    return listOf(
      Project(1, "test", "sample")
    )
  }

  override fun find(id: Int): Project {
    return when (id) {
      1 -> Project(1, "test", "sample")
      else -> Project()
    }
  }

  override fun count(): Int {
    return 1
  }

  override fun insert(project: Project) {}
  override fun update(project: Project) {}
  override fun delete(id: Int) {}
}
