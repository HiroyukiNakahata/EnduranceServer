package com.endurance.handler

import com.endurance.function.isNotEmptyProject
import com.endurance.injector.Injector
import io.ktor.routing.*
import com.endurance.model.IProjectService
import com.endurance.model.Project
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
      val project = call.receiveOrNull() ?: Project(0, "", "")
      when (isNotEmptyProject(project)) {
        false -> call.respond(HttpStatusCode.BadRequest)
        true -> {
          projectService.insertProject(project)
          call.respond(project)
        }
      }
    }

    put {
      val project = call.receiveOrNull() ?: Project(0, "", "")
      when (isNotEmptyProject(project)) {
        false -> call.respond(HttpStatusCode.BadRequest)
        true -> {
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
