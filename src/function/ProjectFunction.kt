package com.endurance.function

import com.endurance.model.Project

fun isNotEmptyProject(project: Project): Boolean = when {
  project.project_name == "" -> false
  project.client == "" -> false
  else -> true
}

