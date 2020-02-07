package com.endurance.handler

import com.endurance.authentication.AuthenticationException
import com.endurance.function.isEmptyAttendee
import com.endurance.model.Attendee
import com.endurance.model.IAttendeeService
import com.endurance.model.IMinutesService
import com.endurance.user
import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveOrNull
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Route.attendeeHandler(
  path: String,
  attendeeService: IAttendeeService,
  minutesService: IMinutesService
) {

  route(path) {
    get {
      val attendees = attendeeService.findByUser(call.user)
      call.respond(attendees)
    }

    get("/{id}") {
      val aid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      val attendee = attendeeService.findByUser(call.user, aid)
      when (attendee.attendee_id) {
        0 -> call.respond(HttpStatusCode.NotFound)
        else -> call.respond(attendee)
      }
    }

    post {
      val attendee = call.receiveOrNull() ?: Attendee()
      val mUid = minutesService.find(attendee.minutes_id).user_id

      when {
        isEmptyAttendee(attendee) -> throw BadRequestException("bad body")
        call.user != mUid -> throw AuthenticationException()
      }

      attendeeService.insert(attendee)
      call.respond(attendee)
    }

    post("/multi") {
      val attendees = (call.receiveOrNull() ?: arrayOf<Attendee>()).toList()

      when (attendees.count()) {
        0 -> throw BadRequestException("bad body")
      }

      when (attendees.any { isEmptyAttendee(it) }) {
        true -> call.respond(HttpStatusCode.BadRequest)
        else -> {
          attendeeService.insertMulti(attendees)
          call.respond(attendees)
        }
      }
    }

    put {
      val attendee = call.receiveOrNull() ?: Attendee()
      val mUid = minutesService.find(attendee.minutes_id).user_id

      when {
        isEmptyAttendee(attendee) -> call.respond(HttpStatusCode.BadRequest)
        call.user != mUid -> call.respond(HttpStatusCode.Unauthorized)
        else -> {
          attendeeService.update(attendee)
          call.respond(attendee)
        }
      }
    }

    delete("/{id}") {
      val aid = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("bad id")

      attendeeService.delete(call.user, aid)
      call.respond(HttpStatusCode.OK)
    }
  }
}
