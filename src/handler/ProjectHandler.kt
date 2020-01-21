package com.endurance.handler

import com.endurance.function.isEmptyProject
import com.endurance.injector.Injector
import com.endurance.model.IProjectService
import com.endurance.model.Project
import io.ktor.routing.*
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond


fun Route.projectHandler(path: String) {
  val projectService: IProjectService = Injector.getProjectService()

  route(path) {
    get {
      val projects = projectService.findProject()
      call.respond(projects)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val project = projectService.findProject(id)
          call.respond(project)
        }
      }
    }

    post {
      val project = call.receiveOrNull() ?: Project()
      when {
        isEmptyProject(project) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          projectService.insertProject(project)
          call.respond(project)
        }
      }
    }

    put {
      val project = call.receiveOrNull() ?: Project()
      when {
        isEmptyProject(project) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          projectService.updateProject(project)
          call.respond(project)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          projectService.deleteProject(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
