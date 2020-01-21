package com.endurance.handler

import com.endurance.function.isEmptyAttendee
import com.endurance.injector.Injector
import com.endurance.model.Attendee
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*

fun Route.attendeeHandler(path: String) {
  val attendeeService = Injector.getAttendeeService()

  route(path) {
    get {
      val attendees = attendeeService.findAttendee()
      call.respond(attendees)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val attendee = attendeeService.findAttendee(id)
          call.respond(attendee)
        }
      }
    }

    post {
      val attendee = call.receiveOrNull() ?: Attendee()
      when {
        isEmptyAttendee(attendee) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.insertAttendee(attendee)
          call.respond(attendee)
        }
      }
    }

    put {
      val attendee = call.receiveOrNull() ?: Attendee()
      when {
        isEmptyAttendee(attendee) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.updateAttendee(attendee)
          call.respond(attendee)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.deleteAttendee(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
