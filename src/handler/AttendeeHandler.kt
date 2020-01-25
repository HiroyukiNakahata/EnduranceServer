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
      val attendees = attendeeService.find()
      call.respond(attendees)
    }

    get("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          val attendee = attendeeService.find(id)
          when (attendee.attendee_id) {
            0 -> call.respond(HttpStatusCode.NotFound)
            else -> call.respond(attendee)
          }
        }
      }
    }

    post {
      val attendee = call.receiveOrNull() ?: Attendee()
      when {
        isEmptyAttendee(attendee) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.insert(attendee)
          call.respond(attendee)
        }
      }
    }

    post("/multi") {
      val attendees = call.receiveOrNull() ?: arrayOf<Attendee>()
      when (attendees.count()) {
        0 -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.insertMulti(attendees.toList())
          call.respond(attendees)
        }
      }
    }

    put {
      val attendee = call.receiveOrNull() ?: Attendee()
      when {
        isEmptyAttendee(attendee) -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.update(attendee)
          call.respond(attendee)
        }
      }
    }

    delete("/{id}") {
      when (val id = call.parameters["id"]?.toIntOrNull()) {
        null -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.delete(id)
          call.respond(HttpStatusCode.OK)
        }
      }
    }
  }
}
