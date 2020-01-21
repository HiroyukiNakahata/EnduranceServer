package com.endurance.handler

import com.endurance.model.IMinutesService
import com.endurance.injector.Injector
import com.endurance.model.Minutes
import com.endurance.function.isEmptyMinutes
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.minutesHandler(path: String) {
  val minutesService: IMinutesService = Injector.getMinutesService()

  route(path) {
    get {
      val minutes = minutesService.findMinutes()
      call.respond(minutes)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val minutes = minutesService.findMinutes(id)
          call.respond(minutes)
        }
      }
    }

    post {
      val minutes = call.receiveOrNull() ?: Minutes()
      when {
        isEmptyMinutes(minutes) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          minutesService.insertMinutes(minutes)
          call.respond(minutes)
        }
      }
    }

    put {
      val minutes = call.receiveOrNull() ?: Minutes()
      when {
        isEmptyMinutes(minutes) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          minutesService.updateMinutes(minutes)
          call.respond(minutes)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          minutesService.deleteMinutes(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
