package com.endurance.handler

import com.endurance.function.isEmptyProject
import com.endurance.model.IProjectService
import com.endurance.model.Project
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.projectHandler(
  path: String,
  projectService: IProjectService
) {

  route(path) {
    get {
      val projects = projectService.find()
      call.respond(projects)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val project = projectService.find(id)
          when (project.project_id) {
            0 -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(project)
          }
        }
      }
    }

    post {
      val project = call.receiveOrNull() ?: Project()
      when {
        isEmptyProject(project) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          projectService.insert(project)
          call.respond(project)
        }
      }
    }

    put {
      val project = call.receiveOrNull() ?: Project()
      when {
        isEmptyProject(project) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          projectService.update(project)
          call.respond(project)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          projectService.delete(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
